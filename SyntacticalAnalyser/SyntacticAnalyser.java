import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class SyntacticAnalyser {

	public static ParseTree parse(List<Token> tokens) throws SyntaxException {
		//Turn the List of Tokens into a ParseTree.


		String[][][] parsingTable = {
				{null,{"PLUS"},{"MINUS"},{"TIMES"},{"DIVIDE"},{"MOD"},{"ASSIGN"},{"EQUAL"},{"NEQUAL"},{"LT"},{"GT"},{"LE"},{"GE"},{"LPAREN"},{"RPAREN"},{"LBRACE"},{"RBRACE"},{"AND"},{"OR"},{"SEMICOLON"},{"PUBLIC"},{"CLASS"},{"STATIC"},{"VOID"},{"MAIN"},{"STRINGARR"},{"ARGS"},{"TYPE"},{"PRINT"},{"WHILE"},{"FOR"},{"IF"},{"ELSE"},{"DQUOTE"},{"SQUOTE"},{"ID"},{"NUM"},{"TRUE"},{"FALSE"},{"CHARLIT"},{"STRINGLIT"}},
				{{"prog"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"PUBLIC", "CLASS", "ID", "LBRACE", "PUBLIC", "STATIC", "VOID", "MAIN", "LPAREN", "STRINGARR", "ARGS", "RPAREN",  "LBRACE", "los", "RBRACE", "RBRACE"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"los"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"epsilon"},null,null,{"stat", "los"},null,null,null,null,null,null,null,{"stat", "los"},{"stat", "los"},{"stat", "los"},{"stat", "los"},{"stat", "los"},null,null,null,{"stat", "los"},null,null,null,null,null},
				{{"stat"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"SEMICOLON"},null,null,null,null,null,null,null,{"decl", "SEMICOLON"},{"print", "SEMICOLON"},{"whilestat"},{"forstat"},{"ifstat"},null,null,null,{"assign", "SEMICOLON"},null,null,null,null,null},
				{{"whilestat"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"WHILE", "LPAREN", "relexpr", "boolexpr", "RPAREN", "LBRACE", "los", "RBRACE"},null,null,null,null,null,null,null,null,null,null,null},
				{{"forstat"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"FOR", "LPAREN", "forstart", "SEMICOLON", "relexpr", "boolexpr", "SEMICOLON", "forarith", "RPAREN", "LBRACE", "los", "RBRACE"},null,null,null,null,null,null,null,null,null,null},
				{{"forstart"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"epsilon"},null,null,null,null,null,null,null,{"decl"},null,null,null,null,null,null,null, {"assign"},null,null,null,null,null},
				{{"forarith"},null,null,null,null,null,null,null,null,null,null,null,null,{"arithexpr"},{"epsilon"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"arithexpr"},{"arithexpr"},null,null,null,null},
				{{"ifstat"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"IF", "LPAREN", "relexpr", "boolexpr", "RPAREN", "LBRACE", "los", "RBRACE", "elseifstat"},null,null,null,null,null,null,null,null,null},
				{{"elseifstat"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"epsilon"},null,null,null,null,null,null,null,null,null,null,{"epsilon"},{"epsilon"},{"epsilon"},{"epsilon"},{"epsilon"},{"elseorelseif", "LBRACE", "los", "RBRACE", "elseifstat"},null,null,{"epsilon"},null,null,null,null,null},
				{{"elseorelseif"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"ELSE", "possif"},null,null,null,null,null,null,null,null},
				{{"possif"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"epsilon"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"IF", "LPAREN", "relexpr", "boolexpr", "RPAREN"},null,null,null,null,null,null,null,null,null},
				{{"assign"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"ID", "ASSIGN", "expr"},null,null,null,null,null},
				{{"decl"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"type", "ID", "possassign"},null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"possassign"},null,null,null,null,null,{"ASSIGN", "expr"},null,null,null,null,null,null,null,null,null,null,null,null,{"epsilon"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"print"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"PRINT", "LPAREN", "printexpr", "RPAREN"},null,null,null,null,null,null,null,null,null,null,null,null},
				{{"type"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"TYPE"},null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"expr"},null,null,null,null,null,null,null,null,null,null,null,null,{"relexpr", "boolexpr"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"charexpr"},{"relexpr", "boolexpr"},{"relexpr", "boolexpr"},{"relexpr", "boolexpr"},{"relexpr", "boolexpr"},null,null},
				{{"charexpr"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"SQUOTE", "CHARLIT", "SQUOTE"},null,null,null,null,null,null},
				{{"boolexpr"},null,null,null,null,null,null,{"boolop", "relexpr", "boolexpr"},{"boolop", "relexpr", "boolexpr"},null,null,null,null,null,{"epsilon"},null,null,{"boolop", "relexpr", "boolexpr"},{"boolop", "relexpr", "boolexpr"},{"epsilon"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"boolop"},null,null,null,null,null,null,{"booleq"},{"booleq"},null,null,null,null,null,null,null,null,{"boollog"},{"boollog"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"booleq"},null,null,null,null,null,null,{"EQUAL"},{"NEQUAL"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"boollog"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"AND"},{"OR"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"relexpr"},null,null,null,null,null,null,null,null,null,null,null,null,{"arithexpr", "relexprprime"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"arithexpr", "relexprprime"},{"arithexpr", "relexprprime"},{"TRUE"},{"FALSE"},null,null},
				{{"relexprprime"},null,null,null,null,null,null,{"epsilon"},{"epsilon"},{"relop", "arithexpr"},{"relop", "arithexpr"},{"relop", "arithexpr"},{"relop", "arithexpr"},null,{"epsilon"},null,null,{"epsilon"},{"epsilon"},{"epsilon"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"relop"},null,null,null,null,null,null,null,null,{"LT"},{"GT"},{"LE"},{"GE"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"arithexpr"},null,null,null,null,null,null,null,null,null,null,null,null,{"term", "arithexprprime"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"term", "arithexprprime"},{"term", "arithexprprime"},null,null,null,null},
				{{"arithexprprime"},{"PLUS", "term", "arithexprprime"},{"MINUS", "term", "arithexprprime"},null,null,null,null,{"epsilon"},null,{"epsilon"},{"epsilon"},{"epsilon"},{"epsilon"},null,{"epsilon"},null,null,null,null,{"epsilon"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"term"},null,null,null,null,null,null,null,null,null,null,null,null,{"factor", "termprime"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"factor", "termprime"},{"factor", "termprime"},null,null,null,null},
				{{"termprime"},{"epsilon"},{"epsilon"},{"TIMES", "factor", "termprime"},{"DIVIDE", "factor", "termprime"},{"MOD", "factor", "termprime"},null,{"epsilon"},null,null,null,null,null,null,{"epsilon"},null,null,null,null,{"epsilon"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null},
				{{"factor"},null,null,null,null,null,null,null,null,null,null,null,null,{"LPAREN", "arithexpr", "RPAREN"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"ID"},{"NUM"},null,null,null,null},
				{{"printexpr"},null,null,null,null,null,null,null,null,null,null,null,null,{"relexpr", "boolexpr"},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,{"DQUOTE", "STRINGLIT", "DQUOTE"},null,{"relexpr", "boolexpr"},{"relexpr", "boolexpr"},{"relexpr", "boolexpr"},{"relexpr", "boolexpr"},null,null}
		};

		if (tokens.size() == 0){ //if empty input syntax exception
			throw new SyntaxException("");
		}

		ParseTree output = new ParseTree();
		Deque<String> stack = new ArrayDeque<>();

		//Add prog to stack and initialise the root
		stack.add("prog");

		TreeNode root = new TreeNode(TreeNode.Label.valueOf(stack.peek()), null);
		output.setRoot(root);
		TreeNode parent = root;
		addRule(stack.pop(), tokens.get(0), stack, parsingTable);
		addChildren(parent, "prog", tokens.get(0).getType().toString(), stack, parsingTable, tokens);

		return output;
	}

	static void addChildren(TreeNode parent, String nonTerm, String terminal, Deque<String> stack, String[][][] parsingTable, List<Token> tokens) throws SyntaxException {
		int num = 0;
		String stackTop = stack.peekLast();
		Token tokenTop = tokens.get(0);

		int column = findColumn(terminal, parsingTable); //find terminal index
		int row = findRow(nonTerm, parsingTable); //find non-terminal index

		String s = ""; //Initialise a string the rule will be turned into

		for (int i = 0; i < parsingTable[row][column].length; i++) {
			s = parsingTable[row][column][i]; //Set the string to the rule being added
			System.out.println(tokenTop.getValue().toString());
			System.out.println(stack);
			try { //tests if the current rule being added is terminal or non-terminal, if non-terminal it throws exception which is caught
				if ((!Token.TokenType.valueOf(s).isVariable()) && s.equals(tokenTop.getType().toString())){ //isVariable() returns false so inverse of it
					parent.addChild(new TreeNode(TreeNode.Label.terminal, tokenTop, parent));
					stack.removeLast();
					tokens.remove(0);
					if (stack.size() == 0){
						break;
					}
				}
			} catch (Exception e){ //If isVariable() doesn't exist then it's either a non-terminal or error which this will catch
				if (s != "epsilon") { //expandable non-terminal
					TreeNode temp = new TreeNode(TreeNode.Label.valueOf(s), parent);
					stack.removeLast();
					parent.addChild(temp);
					addRule(s, tokenTop, stack, parsingTable);
					addChildren(temp, s, tokenTop.getType().toString(), stack, parsingTable, tokens);
				} else { // epsilon
					parent.addChild(new TreeNode(TreeNode.Label.epsilon, parent));
					stack.removeLast();
				}
			}
			if (tokens.size() != 0 && stack.size() != 0) { //if one of stack or tokens = 0 but the other doesn't then wrong input
				stackTop = stack.peekLast();
				tokenTop = tokens.get(0);
			} else {
				throw new SyntaxException("");
			}
		}
	}

	public static void addRule(String target, Token token, Deque<String> stack, String[][][] parsingTable) throws SyntaxException { //Add the rules that exist in the parsing table to the stack

		int column = findColumn(token.getType().toString(), parsingTable);
		int row = findRow(target, parsingTable);
		String s = "";
		try {
			for (int i = parsingTable[row][column].length - 1; i >= 0; i--) {
				s = parsingTable[row][column][i];
				stack.addLast(s);
			}
		} catch (Exception e) { //If there is no rule to add then wrong input
			throw new SyntaxException("");
		}
	}

	public static int findColumn(String target, String[][][] parsingTable){ //find column of the terminal
		int column = 0;
		for (int i = 1; i<parsingTable[0].length; i++){
			if (parsingTable[0][i][0] == target){
				column = i;
				break;
			}
		}
		return column;
	}

	public static int findRow(String target, String[][][] parsingTable){ //Find row of the non-terminal
		int row = 0;
		for (int i = 1; i<= (parsingTable.length - 1); i++){
			if (parsingTable[i][0][0] == target){
				row = i;
				break;
			}
		}
		return row;
	}

}

// The following class may be helpful.

class Pair<A, B> {
	private final A a;
	private final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A fst() {
		return a;
	}

	public B snd() {
		return b;
	}

	@Override
	public int hashCode() {
		return 3 * a.hashCode() + 7 * b.hashCode();
	}

	@Override
	public String toString() {
		return "{" + a + ", " + b + "}";
	}

	@Override
	public boolean equals(Object o) {
		if ((o instanceof Pair<?, ?>)) {
			Pair<?, ?> other = (Pair<?, ?>) o;
			return other.fst().equals(a) && other.snd().equals(b);
		}

		return false;
	}

}

