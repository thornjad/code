;;; test-helper.el --- helper for testing litcoffee-mode

(require 'litcoffee-mode)

(defmacro with-litcoffee-temp-buffer (code &rest body)
  "Insert `code' and enable `coffee-mode'. cursor is beginning of buffer"
  (declare (indent 0) (debug t))
  `(with-temp-buffer
     (insert ,code)
     (goto-char (point-min))
     (litcoffee-mode)
     (font-lock-fontify-buffer)
     ,@body))

(defun forward-cursor-on (pattern &optional count)
  (let ((case-fold-search nil))
    (re-search-forward pattern nil nil (or count 1)))
  (goto-char (match-beginning 0)))

(defun backward-cursor-on (pattern &optional count)
  (let ((case-fold-search nil))
    (re-search-backward pattern nil nil (or count 1)))
  (goto-char (match-beginning 0)))

(defun face-at-cursor-p (face)
  (eq (face-at-point) face))

;;; test-helper.el ends here
