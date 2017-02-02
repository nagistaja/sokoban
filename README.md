# sokoban

Name: Jaak

Description: Simple sokoban game with editor

To run a game:

	java -level levelName.skb

	will create score file automatically with a format of levelName.score
	To see scores: press button "Show high scores"

To run a editor:

	java -editor

	Enter rows & columns + press "Set board size" to create empty level
	Click on scuares to place objects (change value +1) 
	Enter level name without .skb and press save to save it

Game mode colors:
	wall - gray
	player - green
	box - orange
	target - red

Editor mode colors:
	empty - white
	everything else is same as in game mode
	
Licence: MIT
