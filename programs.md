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
a = {1, 2, 3, 4, 5};
b = {2, 4, 6};

union = a or b;

print(union);
"""

program 7 expected output:
"""
[2, 4]
"""