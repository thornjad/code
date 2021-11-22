# hyKoan

hyKoan is the start of my journey to learn Hy. These koans are based on those
[written by taddeimania](https://github.com/taddeimania/hykoans) and inspired by
Google's [lisp-koans](https://github.com/google/lisp-koans).

This has been tested to run with a Hy version as recent as `stable` which as of this writing is 0.16.0.

### Instructions

```
python setup.py install
```

Then run

```
hyk
```

If you see something like the test failure below - you're well on your way!

```python
======================================================================
FAIL: test_assert_truth (koans.a_asserts.Asserts)
We shall contemplate truth by testing reality, via asserts.
----------------------------------------------------------------------
Traceback (most recent call last):
  File "/Users/youruser/hykoans/koans/a_asserts", line 10, in test_assert_truth
    (self.assertTrue (= ____ True)))]
AssertionError: False is not true
----------------------------------------------------------------------
```

### Now What?

The objective of the Koans project is to make each test pass to demonstrate an
understanding of the intention of the unit test and the language at your
fingertips. As you can see from the above test we've got `AssertionError: False
is not true`. In order to correct this test go into `koans/a_asserts.hy` and
change the first test to the following.

```hy
  [test-1-assert-truth
    (fn [self]
      "We shall contemplate truth by testing reality, via asserts."
      (self.assertTrue (= True True)))]
```

of course the fun doesn't end there.  Now you're getting a new failure.

```python
======================================================================
FAIL: test_assert_with_message (koans.a_asserts.Asserts)
Enlightenment may be more easily achieved with appropriate messages.
----------------------------------------------------------------------
Traceback (most recent call last):
  File "/Users/youruser/hykoans/koans/a_asserts.hy", line 15, in test_assert_with_message
    (self.assertTrue (= ____ True) "This should be True, fix it!"))]
AssertionError: This should be True, fix it!
----------------------------------------------------------------------
```
