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

  boolean isColliding(Bird bird) {

    float y = height / 2 - bird.ypos;
    float xOff = xOffset + width / 2;

    float yFromAbove = height - y;

    return(abs(xOff - (xpos + w / 2)) < w / 2 && abs(y - top / 2) < top / 2)
      || (abs(xOff - (xpos + w / 2)) < w / 2 && abs(yFromAbove - (height - bottom) / 2) < (height - bottom) / 2);
  }

  void update() {

    if (xpos + w < 0)
      expired = true;
    else
      xpos -= vel * dt;
  }

  void show() {

    fill(255, 255, 0);

    rect(xpos, 0, w, top);
    rect(xpos, bottom, w, height - bottom);
  }
}
