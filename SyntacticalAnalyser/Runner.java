import java.util.List;

public class Runner {

	public static void main(String[] args) {
		try {
			List<Token> results = LexicalAnalyser.analyse("public class Test { public static void main(String[] args){ int i = 'c'; }}");
			//"public class foo { public static void main(String[] args){ int i = 0; if (i == 2) { i = i + 1; System.out.println(\"Hi\"); } else { i = i * 2; } } }"
			//public class foo { public static void main(String[] args){ int i = 0;} }
			System.out.println(results);
			ParseTree tree = SyntacticAnalyser.parse(results);
			System.out.println(tree);
		} catch (LexicalException e) {
			e.printStackTrace();
		} catch (SyntaxException e) {
			e.printStackTrace();
		}



//TreeNode Example
		// TreeNode assign = new TreeNode(TreeNode.Label.assign, null);
		// TreeNode id = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ID, "x"), assign);
		// TreeNode equal = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ASSIGN), assign);
		// TreeNode expr = new TreeNode(TreeNode.Label.expr, assign);
		// TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, expr);
		// assign.addChild(id);
		// assign.addChild(equal);
		// assign.addChild(expr);
		// expr.addChild(epsilon);
		// equal.addChild(epsilon);

		// ParseTree smallTree = new ParseTree();
		// smallTree.setRoot(assign);

		// System.out.println(smallTree);

	}

}
