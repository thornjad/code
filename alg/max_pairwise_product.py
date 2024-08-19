# python3


def max_pairwise_product(n, a):
    largest, second_largest = 0, 0
    for i in range(0, n):
        if a[i] > largest:
            second_largest = largest
            largest = a[i]
        elif a[i] > second_largest:
            second_largest = a[i]

    return largest * second_largest


if __name__ == '__main__':
    n = int(input())
    a = [int(x) for x in input().split()]
    assert len(a) == n
    print(max_pairwise_product(n, a))

    # from random import randint
    # from time import time

    # while True:
    #     n = randint(2, 50000)
    #     a = [randint(0, 10000) for _ in range(n)]

    #     start = time()
    #     r = max_pairwise_product(n, a)
    #     total_time = time() - start

    #     print(f'total_time: {total_time}, n: {n}, r: {r}')
    #     if total_time > 5.0:
    #         print(f"n: {n}, r: {r}, total_time: {total_time}")
    #         break
