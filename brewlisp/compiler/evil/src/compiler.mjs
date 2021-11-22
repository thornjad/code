import * as os from 'os';

let out = '';

// TODO load these from an external prelude file defining functions
const BUILTINFUNCS = {
	'+': 'plus', // TODO such simple functions can be directly translated to asm
};

const SYSCALLMAP = os.platform() === 'darwin' ? {
	// macOS
	'exit': '0x2000001',
	'write': '0x2000004'
} : {
	// linux
	'exit': 60,
	'write': 1
};

const PARAMREGS = [
	'RDI',
	'RSI',
	'RDX'
];

export default function compile(ast) {
	emitPrefix();
	compileCall(ast[0], ast.slice(1)); // pass in ast first and rest
	emitPostfix();

	return out;
}

function emit(out, depth, instr) {
	const indent = new Array(depth + 1).map(() => '').join('\t');
	out += `${indent}${instr}\n`;
}

function emitPrefix() {
	emit(1, '.global _main\n');
	emit(1, '.text\n');

	// TODO make this just an ADD call
	emit(0, 'plus:');
	emit(1, 'ADD RDI, RSI');
	emit(1, 'MOV RAX, RDI');
	emit(1, 'RET\n');

	emit(0, '_main:');
}

function emitPostfix() {
	// set exit code
	emit(1, 'MOV RDI, RAX');
	emit(1, `MOV RAX, ${SYSCALLMAP['exit']}`);
	emit(1, 'SYSCALL');
}

function compileCall(func, args, dest) {
	// save param regs
	args.map((_, i) => emit(1, `PUSH ${PARAMREGS[i]}`));

	// compile registers and store as params
	args.map((arg, i) => compileArgument(arg, PARAMREGS[i]));

	// make the call
	emit(1, `CALL ${BUILTINFUNCS[func] || func}`);

	// restore param regs
	args.map((_, i) => emit(1, `POP ${PARAMREGS[args.length - 1 - 1]}`));

	if (dest) {
		emit(1, `MOV ${dest}, RAX`);
	}

	emit(0, '');
}

function compileArgument(arg, dest) {
	if (Array.isArray(arg)) {
		// arg is another sexp
		compileCall(arg[0], arg.slice(1), dest);
		return;
	}

	// arg is an atom
	emit(1, `MOV ${dest}, ${arg}`);
}
