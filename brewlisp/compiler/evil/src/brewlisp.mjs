import * as cp from 'child_process';
import * as fs from 'fs';

import parse from './parser';
import compile from './compiler';

function main(args) {
	// TODO use async
	const input = fs.readFileSync(args[2]).toString();
	const ast = parse(input);
	const program = compile(ast);
	console.info(ast);
	console.log();

	try {
		// TODO make async
		try {
			fs.mkdirSync('out');
		} catch (e) {
			if (e.code !== 'EEXIST') {
				// problem other than 'file exists'
				throw e;
			}
		}
		fs.writeFileSync('out/prog.s', program);
		cp.execSync('gcc -mstackrealign -masm=intel -o out/a.out out/prog.s');
		return 0;
	} catch (e) {
		console.error(`Could not output compiled program:`);
		console.error(e);
		return 1;
	}
}

main(process.argv);
