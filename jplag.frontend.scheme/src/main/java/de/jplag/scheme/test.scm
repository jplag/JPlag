(define reload (lambda () (load "hw1.scm")))        ; Handy reload command.

; Question #1 - rotate-n

;   Procedure: rotate
;
;          In: List 'l'.
;
;         Out: List 'l' after single rotation.
;
; Description: Helper function for rotate-n.
;
(define rotate (lambda (l)                          ; Helper function performs
		 (if (null? l)                      ; single rotation.
		     '()
		     (append (cdr l) (list (car l))))))

;   Procedure: rotate-n
;
;          In: List 'l', integer 'n'.
;
;         Out: List 'l', rotated 'n' times.
;
; Description: Rotates list 'l' 'n' times.  Uses helper function rotate.
;
(define rotate-n (lambda (l n)
		   (if (zero? n)
		       l
		       (rotate-n (rotate l) (- n 1)))))

; Question #2 - split

;   Procedure: filter
;
;          In: Function 'f', List 'l'.
;
;         Out: List of all items in 'l' for which function 'f' returns #t.
;
; Description: Helper function for split.
;
(define filter (lambda (f l)                        ; Filter returns a list
		 (if (null? l)                      ; of items in 'l' for which
		     '()                            ; function 'f' is true.
		     (if (f (car l))
			 (cons (car l) (filter f (cdr l)))
			 (filter f (cdr l))))))
;   Procedure: split
;
;          In: List 'l' of integers.
;
;         Out: List containing three sublists -- integers <, ==, and > 0.
;
; Description: Calls helper function filter.
;
                                                    ; Split filters list 'l'
                                                    ; into three lists, of
(define split (lambda (l)                           ; items <, ==, and > 0.
		     (cons (filter (lambda (x) (< x 0)) l)
			   (cons (filter (lambda (x) (eq? x 0)) l)
				 (list (filter (lambda (x) (> x 0)) l))))))

; Question #3 - sum

;   Procedure: sum-helper
;
;          In: List 'l' of integers, and integer 'n'.
;
;         Out: Sum of 'n' and all other integers in 'l'.
;
; Description: Helper function for sum.
;
(define sum-helper (lambda (l n)                    ; Sum-helper passes along
		     (if (null? l)                  ; the running total 'n' of
			 n                          ; every other element
			 (if (null? (cdr l))        ; as it recurses down 'l'.
			     n
			     (sum-helper (cddr l) (+ n (cadr l)))))))
;   Procedure: sum
;
;          In: List 'l' of integers.
;
;         Out: Sum of all integers in 'l'.
;
; Description: Calls helper function sum-helper.
;
(define sum (lambda (l) (sum-helper l 0)))          ; Sum calls sum-helper
                                                    ; with an initial total 0.

; Question #4 - majority?


;   Procedure: major-help?
;
;          In: List 'l', and integer 'n'.
;
;         Out: #t if any item in 'l' occurs more than n/2 times.
;
; Description: Determines majority? with the help of length paramter 'n'.
;
(define major-help? (lambda (l n)                   ; Major-help? detemines how
		      (if (null? l)                 ; many items in list 'l'
			  #f                        ; match the first item.
			  (let* (                   ; If number is strictly >
				 (h (car l))        ; than n/2, it returns #t,
				 (l2 (filter        ; else recurses down 'l'.
				      (lambda (x) (eq? x h))
				      l))
				 (n2 (* 2 (length l2))))
			    (if (> n2 n)
				#t
				(major-help? (cdr l) n))
			    ))))

;   Procedure: majority?
;
;          In: List 'l'.
;
;         Out: #t if any item in 'l' occurs more than half the time.
;
; Description: Calls helper function majority-help? with length of list 'l'.
;
(define majority? (lambda (l)                       ; Majority? calls major- 
		    (major-help? l (length l))))    ; help with length of
                                                    ; list 'l'.

; Question #5 - car&cdr

;   Procedure: member*?
;
;          In: Atom 'x' and complex list 'l'.
;
;         Out: #t if atom 'x' appears in any part of 'l'.
;
; Description: Guides car&cdr down correct portion of complex list.
;
(define member*? (lambda (x lst)
		   (if (null? lst)
		       #f
		       (if (list? (car lst))
			   (or (member*? x (car lst))
			       (member*? x (cdr lst)))
			   (if (eq? x (car lst))
			       #t
			       (member*? x (cdr lst)))))))

;   Procedure: car&cdr2
;
;          In: Atom 's', Complex List 'slst', Function list 'f'.
;
;         Out: Chain of car's and cdr's to reach 's' in complex list 'slst'.
;
; Description: Car&cdr2 recursively calls itself as it moves into the
;              complex list 'slst' toward atom 's'.  At each call, it adds
;              another link in the chain of car's and cdr's by cons-ing
;              them on to the list of functions, 'f'.
;
(define car&cdr2 
  (lambda (s slst f) 
    (if (null? slst)                   ; Slst should never be null,
	f                              ; but just in case...
	(if (eq? (car slst) s)         ; If we've found the atom at the
	    (list 'car f)              ; head, return (car ...).
	    (if (list? (car slst))     ; If the head is a sublist, check to
		(if (member*? s (car slst))  ; see if the atom is in the
		    (car&cdr2 s (car slst) (list 'car f)) ; sublist,
		    (car&cdr2 s (cdr slst) (list 'cdr f))) ; otherwise,
		(car&cdr2 s (cdr slst) (list 'cdr f)) ; proceed down the
		)))))                  ; tail of the list.
;
;   Procedure: car&cdr
;
;          In: Atom 's', Complex List 'slst', Error value 'errvalue'.
;
;         Out: Chain of car's and cdr's to reach 's' in complex list 'slst',
;              or 'errvalue' if 's' is not in 'slst'.
;
; Description: Car&cdr determines if 's' is in 'slst' using helper function
;              member*?.  If it is not, it returns 'errvalue'.  
;              If it is, then it calls helper function car&cdr2 to 
;              assemble the chain of car's and cdr's to reach 's'.
;
(define car&cdr (lambda (s slst errvalue)
		  (if (member*? s slst)
		      (list 'lambda '(lst)
			    (car&cdr2 s slst 'lst))
		      errvalue)))

; Question #6 - matrix-multiply
;
; Procedure: matrix-multiply
;
;        In: Two lists of four integers each.
;
;       Out: A list of four integers representing the 2x2 matrix result
;            of matrix-multiplying the two input matrices.

(define matrix-multiply (lambda (m1 m2)
			  (let ((a11 (car m1))
				(a12 (cadr m1))
				(a21 (caddr m1))
				(a22 (cadddr m1))
				(b11 (car m2))
				(b12 (cadr m2))
				(b21 (caddr m2))
				(b22 (cadddr m2)))
			  (cons (+ (* a11 b11)
				   (* a12 b21))
				(cons (+ (* a11 b12)
					 (* a12 b22))
				      (cons (+ (* a21 b11)
					       (* a22 b21))
					    (list (+ (* a21 b12)
						     (* a22 b22)
						     )
						  )
					    )
				      )
				)
			  )
			  )
  )

; Question 7 - extensional-eq?
;
; Procedure: extensional-eq?
;
;        In: Two functions (each of which take a single integer argument),
;            and an integer, 'n'.
;
;       Out: #t if the two function-arguments return the same value for all
;            integers from 0-n.  #f if they do not.

(define extensional-eq? (lambda (f1 f2 n)
			  (if (< n 0)
			      #t
			      (if (not (eq? (f1 n) (f2 n)))
				  #f
				  (extensional-eq? f1 f2 (- n 1))
				  )
			      )
			  )
  )

; Question #8 - acc
;
;   Procedure: acc-helper
;
;          In: List of integers 'lst', and initial sum 'n'.
;
;         Out: List of integers, where each integer is the integer from the
;              input list added to the summation of all previous integers
;              in the list.
;
; Description: Helper function for acc.

(define acc-helper (lambda (lst n)
		     (if (null? lst)
			 '()
			 (cons (+ n (car lst))
			       (acc-helper (cdr lst)
					   (+ (car lst) n)
					   )
			       )
			 )
		     )
  )

;   Procedure: acc
;
;          In: List of integers
;
;         Out: List of integers, where each integer is the integer from the
;              input list added to the summation of all previous integers
;              in the list.
;
; Description: Calls acc-helper with initial list, and initial sum 0.

(define acc (lambda (lst) (acc-helper lst 0)))

; ...