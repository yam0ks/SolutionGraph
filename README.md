## About:books:
This application allows you to solve linear programming problems using the simplex method and the graphical method (similar to the Desmos graphing calculator). 
The user needs to specify the number of constraints and variables in them, then enter the coefficients for each constraint and indicate the sign (≥ / ≤). 
Next, you should specify the coefficients of the optimization function and the direction (maximize / minimize).

If the simplex method is selected, the result of the program will be a detailed step-by-step solution, if the graphical method is selected, 
the answer will be a point on the graph of functions that falls under all restrictions and is a minimum / maximum.

## Examples:eyes:
**For example, our task will look like this:**

$$
\begin{cases}
-3x - 4y < -44 \\ 
-3x + 3y < 3 \\ 
-5x + 2y \to max
\end{cases}
$$

**Then the solution using the graphical method will be as follows:**

![image](https://user-images.githubusercontent.com/78639838/177058880-8b120256-0f40-4414-b920-9f19dd689025.png)

**If we change the goal to the minimum:**

$$
\begin{cases}
-3x - 4y < -44 \\ 
-3x + 3y < 3 \\ 
-5x + 2y \to min
\end{cases}
$$

**Then the solution using the simplex method will be as follows (no solution):**

![image](https://user-images.githubusercontent.com/78639838/177059012-d16dd0a4-f937-4346-aa83-823b71f843ae.png)
