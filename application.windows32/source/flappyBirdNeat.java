import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import matrix.math.*; 
import neatAlgorithm.*; 
import java.util.Collections; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class flappyBirdNeat extends PApplet {





float dt = 0.1f;
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

public void setup() {

  NeatGenome.parent = this;
  

  if (humanPlaying)
    player = new Bird();
  else
    pop = new Population(500);

  PM = new PipeManager();
}

public void draw() {

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
class Bird implements Comparable<Bird> {

  float vel, ypos, radius, lift;

  NeatGenome brain;

  boolean crashed;

  float score;

  Bird() {

    ypos = 0;
    vel = 0;
    lift = 120;

    score = 0;

    radius = 8;

    brain = new NeatGenome(false);

    crashed = false;
  }

  Bird(NeatGenome otherBrain) {

    ypos = 0;
    vel = 0;
    lift = 120;

    score = 0;

    radius = 8;

    brain = otherBrain;

    crashed = false;
  }

  public void update(ArrayList<Pipe> pipes) {

    score++;
    float y = height / 2 - ypos;

    Pipe closest = getClosest(pipes);
    score -= abs((closest.bottom + closest.top) / 2 - y) / height;

    if (ypos <= - height / 2 + radius && vel < 0)
      crashed = true; //ypos = - height / 2 + radius;
    else if (ypos >= height / 2 - radius && vel > 0)
      crashed = true; //ypos = height / 2 - radius;
    else
      ypos += vel * dt;
  }

  public void applyForce(float force) {

    vel += force * dt;
  }

  public Pipe getClosest(ArrayList<Pipe> pipes) {

    Pipe closest = null;
    float compDist = 10 * width;
    float xOff = xOffset + width / 2;

    for (Pipe pipe : pipes) {

      float distance = pipe.xpos + pipe.w - xOff;

      if (distance < compDist && distance > 0) {
        compDist = distance;
        closest = pipe;
      }
    }

    return closest;
  }

  public void think(ArrayList<Pipe> pipes) {

    float[] inputs = new float[5];

    Pipe closest = getClosest(pipes);

    inputs[0] = vel;
    inputs[1] = 2 * ypos / height;
    inputs[2] = 2 * closest.bottom / height;
    inputs[3] = 2 * closest.top / height;
    inputs[4] = 2 * closest.xpos / width;

    //println(closest.top);

    float[] outputs = brain.computeOutputs(inputs);

    if (outputs[0] > 0.5f)
      lift();
  }

  public void lift() {

    vel = 120;
  }

  public void show() {

    push();

    translate(width / 2, height / 2);
    scale(1, -1);

    fill(255);
    ellipse(xOffset, ypos, 2 * radius, 2 * radius);

    pop();
  }

  public int compareTo(Bird other) {

    if (other.score > score)
      return - 1;
    else if (other.score == score)
      return 0;
    else
      return 1;
  }
}
class Pipe {

  float spacing, w;

  boolean expired, hasGivenScore;

  float edgeLimit;

  float top, bottom;
  float xpos, vel;

  Pipe() {

    edgeLimit = 0;
    w = 100;

    xpos = width;
    spacing = space;

    top = random(edgeLimit, height - spacing - edgeLimit);
    bottom = top + spacing;

    vel = 50;

    expired = false;
    hasGivenScore = false;
  }

  public boolean isColliding(Bird bird) {

    float y = height / 2 - bird.ypos;
    float xOff = xOffset + width / 2;

    float yFromAbove = height - y;

    return(abs(xOff - (xpos + w / 2)) < w / 2 && abs(y - top / 2) < top / 2)
      || (abs(xOff - (xpos + w / 2)) < w / 2 && abs(yFromAbove - (height - bottom) / 2) < (height - bottom) / 2);
  }

  public void update() {

    if (xpos + w < 0)
      expired = true;
    else
      xpos -= vel * dt;
  }

  public void show() {

    fill(255, 255, 0);

    rect(xpos, 0, w, top);
    rect(xpos, bottom, w, height - bottom);
  }
}
class PipeManager {

  ArrayList<Pipe> pipes;

  int cooldown, count;

  PipeManager() {

    pipes = new ArrayList<Pipe>();

    cooldown = 100;
    count = 0;
  }

  public void controlCollisions(ArrayList<Bird> birds) {

    for (Bird bird : birds) {
      for (Pipe pipe : pipes) {
        if (pipe.isColliding(bird))
          bird.crashed = true;
      }
    }
  }

  public void controlCollisions(Bird player) {

    for (Pipe pipe : pipes) {
      if (pipe.isColliding(player))
        player.crashed = true;
    }
  }

  public void giveScore() {

    Pipe first = pipes.get(0);

    if (!first.hasGivenScore && first.xpos + first.w - xOffset - width / 2 < 0) {

      first.hasGivenScore = true;
      score++;
    }
  }

  public void reset() {

    count = 0;
    pipes = new ArrayList<Pipe>();
  }

  public void addPipes() {

    if (count == 0) {

      count = cooldown;
      pipes.add(new Pipe());
    }
  }

  public void updatePipes() {

    if (count > 0)
      count--;

    for (Pipe pipe : pipes)
      pipe.update();
  }

  public void removePipes() {

    for (int i = pipes.size() - 1; i >= 0; i--) {

      Pipe pipe = pipes.get(i);
      if (pipe.expired)
        pipes.remove(i);
    }
  }

  public void showPipes() {

    for (Pipe pipe : pipes)
      pipe.show();
  }
}
class Population {

  ArrayList<Bird> birds;
  ArrayList<NeatGenome> genomes;

  Bird[] deadBirds;

  Speciator specieManager;

  int generation;

  Population(int N) {
    
    generation = 1;

    NeatGenome.inputs = 5;
    NeatGenome.outputs = 1;
    NeatGenome.sigmoid();
    NeatGenome.applyToAll = false;

    birds = new ArrayList<Bird>();
    deadBirds = new Bird[N];

    genomes = new ArrayList<NeatGenome>();

    for (int i = 0; i < N; i++) {

      deadBirds[i] = new Bird();
      birds.add(deadBirds[i]);
    }

    for (Bird bird : birds)
      genomes.add(bird.brain);

    specieManager = new Speciator(genomes, 2.5f, 1, 1, 0.4f); //2 1 1 0.4
    NeatGenome.specieManager = specieManager;

    for (NeatGenome brain : genomes) {
      while (brain.connectionGenes.size() < stCon)
        brain.mutateConnection();
    }

    specieManager.speciate();
  }

  public void think(ArrayList<Pipe> pipes) {

    for (Bird bird : birds)
      bird.think(pipes);
  }

  public void calcFitness() {

    int totalScore = 0;

    for (int i = 0; i < deadBirds.length; i++)
      totalScore += deadBirds[i].score;

    for (int i = 0; i < deadBirds.length; i++)
      deadBirds[i].brain.fitness = deadBirds[i].score / totalScore;
  }

  public void getNewPop() {
    
    calcFitness();

   //println(specieManager.set.get(0).size());
    specieManager.generateNewOffspring();

    specieManager.mutateParameters(0.3f, 1, false); //0.5 1
    specieManager.mutateNodes(0.1f); //0.1
    specieManager.mutateConnections(0.1f); //0.1

    //specieManager.speciate();

    for (int i = 0; i < deadBirds.length; i++) {
      deadBirds[i] = new Bird(genomes.get(i)); 
      birds.add(deadBirds[i]);
    }
    
    specieManager.speciate();
  }

  public void removeCrashed() {

    for (int i = birds.size() - 1; i >= 0; i--) {

      Bird bird = birds.get(i);
      if (bird.crashed) {


        birds.remove(i);
      }
    }
  }

  public void update(ArrayList<Pipe> pipes) {

    for (Bird bird : birds) {
      bird.update(pipes);
      bird.applyForce(force);
    }
  }

  public void show() {

    for (Bird bird : birds)
      bird.show();
  }
}
public void keyPressed() {

  if (!humanPlaying) {

    if (!neatVisualizer) {
      if (keyCode == RIGHT)
        framesPerInt += 10;
      else if (keyCode == LEFT && framesPerInt > 9)
        framesPerInt -= 10;
      else if (keyCode == ENTER)
        framesPerInt = 1;
      else if (keyCode == UP)
        space += 5;
      else if (keyCode == DOWN)
        space -= 5;
      else if (keyCode == TAB) {

        neatVisualizer = !neatVisualizer;
        sortBirdArray(pop.deadBirds);
        chosen = pop.deadBirds[index];
      }
    } else {
      if (keyCode == RIGHT && index < pop.genomes.size() - 1)
        chosen = pop.deadBirds[index++ + 1];
      else if (keyCode == LEFT && index > 0)
        chosen = pop.deadBirds[index-- - 1];
      else if (keyCode == TAB)
        neatVisualizer = !neatVisualizer;
    }
  } else {
    if (key == 'w')
      player.lift();
  }
}

public void mousePressed(){
  if (neatVisualizer)
    chosen.brain.select(mouseX, mouseY);
}

public void sortBirdArray(Bird[] array){
  
  ArrayList<Bird> birds = new ArrayList<Bird>(array.length);
  for (int i = 0; i < array.length; i++)
    birds.add(array[i]);
  
  Collections.sort(birds);
  Collections.reverse(birds);
  
  for (int i = 0; i < array.length; i++)
    array[i] = birds.get(i);
}
  public void settings() {  size(1280, 720); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "flappyBirdNeat" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
