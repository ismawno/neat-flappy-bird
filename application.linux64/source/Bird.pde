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

  void update(ArrayList<Pipe> pipes) {

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

  void applyForce(float force) {

    vel += force * dt;
  }

  Pipe getClosest(ArrayList<Pipe> pipes) {

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

  void think(ArrayList<Pipe> pipes) {

    float[] inputs = new float[5];

    Pipe closest = getClosest(pipes);

    inputs[0] = vel;
    inputs[1] = 2 * ypos / height;
    inputs[2] = 2 * closest.bottom / height;
    inputs[3] = 2 * closest.top / height;
    inputs[4] = 2 * closest.xpos / width;

    //println(closest.top);

    float[] outputs = brain.computeOutputs(inputs);

    if (outputs[0] > 0.5)
      lift();
  }

  void lift() {

    vel = 120;
  }

  void show() {

    push();

    translate(width / 2, height / 2);
    scale(1, -1);

    fill(255);
    ellipse(xOffset, ypos, 2 * radius, 2 * radius);

    pop();
  }

  int compareTo(Bird other) {

    if (other.score > score)
      return - 1;
    else if (other.score == score)
      return 0;
    else
      return 1;
  }
}
