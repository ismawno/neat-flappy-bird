void keyPressed() {

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

void mousePressed(){
  if (neatVisualizer)
    chosen.brain.select(mouseX, mouseY);
}

void sortBirdArray(Bird[] array){
  
  ArrayList<Bird> birds = new ArrayList<Bird>(array.length);
  for (int i = 0; i < array.length; i++)
    birds.add(array[i]);
  
  Collections.sort(birds);
  Collections.reverse(birds);
  
  for (int i = 0; i < array.length; i++)
    array[i] = birds.get(i);
}
