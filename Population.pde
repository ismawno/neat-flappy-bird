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

    specieManager = new Speciator(genomes, 2.5, 1, 1, 0.4); //2 1 1 0.4
    NeatGenome.specieManager = specieManager;

    for (NeatGenome brain : genomes) {
      while (brain.connectionGenes.size() < stCon)
        brain.mutateConnection();
    }

    specieManager.speciate();
  }

  void think(ArrayList<Pipe> pipes) {

    for (Bird bird : birds)
      bird.think(pipes);
  }

  void calcFitness() {

    int totalScore = 0;

    for (int i = 0; i < deadBirds.length; i++)
      totalScore += deadBirds[i].score;

    for (int i = 0; i < deadBirds.length; i++)
      deadBirds[i].brain.fitness = deadBirds[i].score / totalScore;
  }

  void getNewPop() {
    
    calcFitness();

   //println(specieManager.set.get(0).size());
    specieManager.generateNewOffspring();

    specieManager.mutateParameters(0.3, 1, false); //0.5 1
    specieManager.mutateNodes(0.1); //0.1
    specieManager.mutateConnections(0.1); //0.1

    //specieManager.speciate();

    for (int i = 0; i < deadBirds.length; i++) {
      deadBirds[i] = new Bird(genomes.get(i)); 
      birds.add(deadBirds[i]);
    }
    
    specieManager.speciate();
  }

  void removeCrashed() {

    for (int i = birds.size() - 1; i >= 0; i--) {

      Bird bird = birds.get(i);
      if (bird.crashed) {


        birds.remove(i);
      }
    }
  }

  void update(ArrayList<Pipe> pipes) {

    for (Bird bird : birds) {
      bird.update(pipes);
      bird.applyForce(force);
    }
  }

  void show() {

    for (Bird bird : birds)
      bird.show();
  }
}
