class PipeManager {

  ArrayList<Pipe> pipes;

  int cooldown, count;

  PipeManager() {

    pipes = new ArrayList<Pipe>();

    cooldown = 100;
    count = 0;
  }

  void controlCollisions(ArrayList<Bird> birds) {

    for (Bird bird : birds) {
      for (Pipe pipe : pipes) {
        if (pipe.isColliding(bird))
          bird.crashed = true;
      }
    }
  }

  void controlCollisions(Bird player) {

    for (Pipe pipe : pipes) {
      if (pipe.isColliding(player))
        player.crashed = true;
    }
  }

  void giveScore() {

    Pipe first = pipes.get(0);

    if (!first.hasGivenScore && first.xpos + first.w - xOffset - width / 2 < 0) {

      first.hasGivenScore = true;
      score++;
    }
  }

  void reset() {

    count = 0;
    pipes = new ArrayList<Pipe>();
  }

  void addPipes() {

    if (count == 0) {

      count = cooldown;
      pipes.add(new Pipe());
    }
  }

  void updatePipes() {

    if (count > 0)
      count--;

    for (Pipe pipe : pipes)
      pipe.update();
  }

  void removePipes() {

    for (int i = pipes.size() - 1; i >= 0; i--) {

      Pipe pipe = pipes.get(i);
      if (pipe.expired)
        pipes.remove(i);
    }
  }

  void showPipes() {

    for (Pipe pipe : pipes)
      pipe.show();
  }
}
