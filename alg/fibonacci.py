# Uses python3
def calc_fib(n):
    if n <= 1:
        return n

    fs = [0, 1]
    for i in range(2, n + 1):
        fs.append(fs[i - 1] + fs[i - 2])

    return fs[n]


n = int(input())
print(calc_fib(n))
