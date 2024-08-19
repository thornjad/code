# Uses python3
import sys


def get_fib_last_digit(n):
    if n <= 1:
        return n

    fs = [0, 1]
    for i in range(2, n + 1):
        fs.append((fs[i - 1] + fs[i - 2]) % 10)

    return fs[n]


if __name__ == '__main__':
    input = sys.stdin.read()
    n = int(input)
    print(get_fib_last_digit(n))
