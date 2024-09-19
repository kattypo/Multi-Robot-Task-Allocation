# Multi-Robot-Task-Allocation
Program that allocates robots to complete a set of tasks with 2 visualizers. User is prompted to choose a visualizer. Visualizer 1 randomly generates garbage and robot positions with no obstacles. Visualizer 2 randomly generates garbage and robot positions with obstacles. Garbage is represented by green squares, robots are represented by gray squares, and obstacles are represented by pink squares.  

Each display is implemented with JFrame and JPanels. A JPanel represents the environment and current positions of the entities along with a JPanel that holds a button at the top of the display that progresses the environment forward in time. Environment is a 50x50 grid. Set of 20 robots is generated along with 40 garbage piles and if visualizer 2 is selected there are 20 obstacles.

Update 09/19/2024:
Removed the button to progress the environment and implemented a automatic 1 second timer which continues the demo environment. Top JPanel is now a placeholder for future UI elements to be added.
