;; test-highlighting.el --- highlighting test for literate-coffee-mode

(require 'ert)
(require 'litcoffee-mode)

(ert-deftest not-highlighting-not-indented-part ()
  "Not highlight not indented part"
  (let ((coffee-tab-width 2))
    (with-litcoffee-temp-buffer
      "
if true
  console.log '/', foo"
      (forward-cursor-on "if")
      (should (not (eq (face-at-point) 'font-lock-keyword-face))))))

(ert-deftest highlight-indented-part-as-coffeescript ()
  "Highlight indented part as CoffeeScript."
  (let ((coffee-tab-width 2))
    (with-litcoffee-temp-buffer
      "
    if true
      console.log '/', foo"
      (forward-cursor-on "if")
      (should (face-at-cursor-p 'font-lock-keyword-face)))))

(ert-deftest highlight-indented-part-by-tabs-as-coffeescript ()
  "Highlight indented part by tabs as CoffeeScript."
  (let ((coffee-tab-width 2))
    (with-litcoffee-temp-buffer
      "
\tif true
\t  console.log '/', foo"
      (forward-cursor-on "if")
      (should (face-at-cursor-p 'font-lock-keyword-face)))))

(ert-deftest highlighting-slash ()
  "Regression test for #5"
  (with-litcoffee-temp-buffer
    "    console.log '/', foo"
    (forward-cursor-on "foo")
    (should (not (eq (face-at-point) 'font-lock-string-face)))))

;;; test-highlighting.el ends here
