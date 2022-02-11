package cz.diheluk.various;

public class SimpleAst {
    public static void main(String[] args) {

        //String s = "3 + 6 * 2";
        //double expected = 3 + 6 * 2;
        String s = "0 + (3 + 6 * 2) * 8 - 2";
        double expected = (3 + 6 * 2) * 8 - 2;
        //String s = "5 * (3 + 6 * 2) + 4 + (1 - 6 + 3) * (10 * 2)";
        //double expected = 5 * (3 + 6 * 2) + 4 + (1 - 6 + 3) * (10 * 2);

        array = s.toCharArray();
        Tree tree = new Tree();

        if(false) {
            Token token = lexer();
            while (token != null) {
                System.out.println(token);
                token = lexer();
            }
        } else {
            tree.root = generateTree();
        }

        //preorder(tree.root);

        System.out.println(tree.root);

        System.out.println("actual: " + evaluateTree(tree.root));
        System.out.println("expected: " + expected);
    }

    private enum Type {
        OPERATOR, NUMBER;
    }

    private static class Token {
        Type type;
        double value;
        char operator;

        public Token(Type type, double value, char operator) {
            this.type = type;
            this.value = value;
            this.operator = operator;
        }

        @Override
        public String toString() {
            if(type == Type.OPERATOR) {
                return operator + "";
            }
            return String.valueOf(value);
        }
    }

    private static char[] array;
    private static int i = 0;
    private static final StringBuilder sb = new StringBuilder();
    private static Token lexer() {
        for(;i < array.length; ++i) {
            if(array[i] == ' ') {
                continue;
            }

            if(Character.isDigit(array[i])) {
                sb.setLength(0);
                sb.append(array[i++]);

                while(i < array.length && (Character.isDigit(array[i]) || array[i] == '.' || array[i] == 'E')) {
                    sb.append(array[i++]);
                }

                return new Token(Type.NUMBER, Double.parseDouble(sb.toString()), ' '); // todo type LBRACKET
            } else if(array[i] == '(') {
                ++i;
                return new Token(Type.OPERATOR, 0, '('); // todo type RBRACKET
            } else if(array[i] == ')') {
                ++i;
                return new Token(Type.OPERATOR, 0, ')');
            } else if(array[i] == '+' || array[i] == '-' || array[i] == '*' || array[i] == '/') {
                return new Token(Type.OPERATOR, 0, array[i++]);
            }
        }

        return null;
    }

    private static class Tree {
        Node root;
    }

    private static class Node {
        Token token;
        Node left, right;

        public Node(Token token) {
            this.token = token;
        }

        public StringBuilder toString(StringBuilder prefix, boolean isTail, StringBuilder sb) {
            if(right!=null) {
                right.toString(new StringBuilder().append(prefix).append(isTail ? "│   " : "    "), false, sb);
            }
            sb.append(prefix).append(isTail ? "└── " : "┌── ").append(token.toString()).append("\n");
            if(left!=null) {
                left.toString(new StringBuilder().append(prefix).append(isTail ? "    " : "│   "), true, sb);
            }
            return sb;
        }

        @Override
        public String toString() {
            return this.toString(new StringBuilder(), true, new StringBuilder()).toString();
        }
    }

    private static double evaluateTree(Node node) {
        if(node.token.type == Type.NUMBER) {
            return node.token.value;
        }

        switch (node.token.operator) {
            case '+':
                return evaluateTree(node.left) + evaluateTree(node.right);

            case '-':
                return evaluateTree(node.left) - evaluateTree(node.right);

            case '*':
                return evaluateTree(node.left) * evaluateTree(node.right);

            case '/': // todo zero check
                return evaluateTree(node.left) / evaluateTree(node.right);

            default:
                throw new IllegalStateException("Incorrect operator!");
        }
    }

    private static Node generateTree () {
        Node root = null;
        Node currentLayer = null;
        Node tmp;

        // todo expressions starting with '(; not working correctly; double brackets not working
        Token token = lexer();
        if(token != null) {
            if (token.operator == '(') { //recurse
                root = generateTree();
                currentLayer = root;
            } else {
                tmp = new Node(token);
                root = tmp;
                currentLayer = tmp;
            }
        } else {
            return null;
        }

        token = lexer();
        while (token != null) {
            tmp = new Node(token);

            if(root == null) {
               root = tmp;
               currentLayer = tmp;
            } else if(token.type == Type.OPERATOR) {
                if(root.token.type == Type.NUMBER) {
                    tmp.left = root;
                    root = tmp;
                    currentLayer = root;
                } else {  //root is operator -> check current operator
                    if(token.operator == '+' || token.operator == '-') {  //add layer up
                        tmp.left = root;
                        root = tmp;
                        currentLayer = root;
                    } else if(token.operator == '*' || token.operator == '/') {  //add layer down
                        tmp.left = currentLayer.right;
                        currentLayer.right = tmp;
                        currentLayer = tmp;
                    } else if(token.operator == '(') { //recurse
                        currentLayer.right = generateTree();
                    } else if(token.operator == ')') { //return
                        return root;
                    }
                }
            } else if(token.type == Type.NUMBER) {
                currentLayer.right = tmp;
            }

            token = lexer();
        }

        return root;
    }

    private static void inorder(Node node) {
        if(node != null) {
            inorder(node.left);
            System.out.println(node.token);
            inorder(node.right);
        }
    }

    private static void preorder(Node node) {
        if(node != null) {
            System.out.println(node.token);
            preorder(node.left);
            preorder(node.right);
        }
    }
}
