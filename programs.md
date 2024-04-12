program1:

x = "Hello";
y = "World";
print(x ++ " " ++ y);

program 1 expected outout:
"""
Hello World
"""

program2:

x = "woof ";
y = "Dog goes " ++ (x * 2);
print(y);

program 2 expected outout:
"""
Dogs goes woof woof 
"""

program3:

sum = 0
for(i in 10..20) {
  sum = sum + i;
}
print(sum)

program 3 expected outout:
"""
165
"""

program4:
"""
function greeting(name, message) {
  x = "Hi,";
  x = x ++ " my name is " ++ name ++ ".";
  print(x);
  print(message);
}
greeting("Albert", "How are you?");
"""

program 4 expected outout:
"""
Hi, my name is Albert.
How are you?
"""

program5:

function factorial(n) {
  if(n < 2) {
    1;
  } else {
    n * factorial(n-1);
  }
}
print(factorial(10));

program 5 expected outout:
"""
3628800
"""

program6:

"""
list = [1, 2, 3, 4, 5];

print(list.length());
print(list.sum());
print(list)
"""

program 6 expected output:
"""
5
15
[1, 2, 3, 4, 5]
"""

program7:

"""
sum = 0 // no parenthesis needed
/* this is a loop */
for(i in 10..20) {
  sum = sum + i; /* this is a comment */
}

print(sum) // printing
"""

program 7 expected output:
"""
165
"""

program8a:

"""
function funca(a, b) {
  print("normal function:");
  ans = a + b;
  print(ans);
}

funca(10, 5);
"""

program 8a expected output:
"""
normal function:
15
"""

program8b:

"""
funcb = (a, b) >> {
  print("arrow function:");
  ans = a + b;
  print(ans);
}

funcb(20, 5);
"""

program 8b expected output:
"""
arrow function:
25
"""