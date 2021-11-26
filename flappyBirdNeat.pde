import matrix.math.*;
import neatAlgorithm.*;
import java.util.Collections;

float dt = 0.1;
float force = - 100;

float xOffset = - 600;

int framesPerInt = 1;
int space = 150;

int score = 0;
int maxScore = 0;

int stCon = 5;

boolean humanPlaying = false;
boolean neatVisualizer = false;
Bird chosen;
int index = 0;

PApplet sketch = this;

PipeManager PM;
Population pop;

Bird player;

void setup() {

  NeatGenome.parent = this;
  size(1280, 720);

  if (humanPlaying)
    player = new Bird();
  else
    pop = new Population(500);

  PM = new PipeManager();
}

void draw() {

  background(0);

  if (!humanPlaying) {
    if (!neatVisualizer) {

      for (int i = 0; i < framesPerInt; i++) {

        PM.addPipes();
        PM.updatePipes();
        PM.removePipes();
        PM.controlCollisions(pop.birds);
        PM.giveScore();

        pop.update(PM.pipes);
        pop.think(PM.pipes);
        pop.removeCrashed();

        if (pop.birds.size() == 0) {
          PM.reset();
          pop.getNewPop();
          pop.generation++;

          if (score > maxScore)
            maxScore = score;

          score = 0;
        }
      }

      pop.show();
      PM.showPipes();

      textSize(35);
      fill(255);

      text("Generation: " + pop.generation, 40, height - 40);

      textSize(20);

      text("Spacing: " + space, 40, height - 15);
      text("Speed: " + framesPerInt, 180, height - 15);

      text("Score: " + score, 40, height - 75);
      text("MaxScore: " + maxScore, 180, height - 75);

      text("Species: " + pop.specieManager.getCount(), 40, 25);
    } else {
      if (!chosen.brain.hasVisualizer)
        chosen.brain.initializeVisualizer();
        
      stroke(255);
      chosen.brain.display();
      
      textSize(35);
      fill(255);
      text("Genome: " + (index + 1), 40, height - 40);
      
      textSize(20);
      text("Score: " + chosen.score, 40, height - 15);
    }
  } else {

    PM.addPipes();
    PM.updatePipes();
    PM.removePipes();
    PM.controlCollisions(player);
    PM.giveScore();

    player.update(PM.pipes);
    player.applyForce(force);

    if (player.crashed) {
      PM.reset();
      player = new Bird();

      maxScore = score;
      score = 0;
    }


    player.show();
    PM.showPipes();

    textSize(20);
    fill(255);

    text("Score: " + score, 40, height - 15);
    text("MaxScore: " + maxScore, 180, height - 15);
  }
}
