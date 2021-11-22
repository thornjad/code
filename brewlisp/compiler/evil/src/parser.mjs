export default function parse(program) {
	const ast = [];
	let curr = '';

	let i = 0;
	while (i < program.length) {
		const char = program.charAt(i);

		switch (char) {
		case '(':
			// begin new sexp
			const [parsed, rest] = parse(program.substring(i + 1));
			ast.push(parsed);
			program = rest;
			i = 0;
			break;
		case ')':
			// end sexp
			ast.push(+curr || curr); // cast to number if possible
			return [ast, program.substring(++i)];
		case ' ':
			// end of word
			ast.push(+curr || curr);
			curr = '';
			break;
		default:
			curr += char;
			break;
		}

		i++;
	}

	return [ast, ''];
}
