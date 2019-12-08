package edu.neu.coe.info6205.life.base;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


import io.jenetics.*;

import io.jenetics.util.RandomRegistry;
import io . jenetics .BitChromosome;
import io . jenetics . BitGene ;
import io . jenetics . Genotype ;
import io . jenetics . Mutator ;
import io . jenetics . Phenotype ;
import io . jenetics . RouletteWheelSelector ;
import io . jenetics . SinglePointCrossover ;


//Main class
public class GeneticAlgorithm {

    Population population = new Population();
    Individual fittest;
    Individual secondFittest;
    int generationCount = 0;
    //Game game = new Game();

    public static void main(String[] args) {

//        List<Genotype<DoubleGene>> genotypes =
//                RandomRegistry.with( new Random(123),r->{Genotype.of(DoubleChromosome.of(0.0,100.0,10)).instances().limit(100).collect(Collectors.toList())});
//                //RandomRegistry.with(new Random(123) , r -> { Genotype.of(DoubleChromosome.of(0.0, 100.0, 10)). instances () . limit (100).collect(Collectors.toList())});

        Random rn = new Random();

        GeneticAlgorithm demo = new GeneticAlgorithm();

        //Initialize population
        demo.population.initializePopulation(100);

        //Calculate fitness of each individual
        demo.population.calculateFitness();

        //System.out.println("Generation: " + demo.generationCount + " Fittest: " + demo.population.fittest);

        //While population gets an individual with maximum fitness
        while (demo.population.fittest < 1000) {
            ++demo.generationCount;

            //Do selection
            demo.selection();

            //Do crossover
            demo.crossover();

            //Do mutation under a random probability
            if (rn.nextInt()%7 < 5) {
                demo.mutation();
            }

            //Add fittest offspring to population
            demo.addFittestOffspring();

            //Calculate new fitness value
            demo.population.calculateFitness();

            System.out.println("Generation: " + demo.generationCount + " Fittest: " + demo.population.fittest);
        }

        //System.out.println("\nSolution found in generation " + demo.generationCount);
        System.out.println("\nSolution found" );
        System.out.println("Fitness: "+demo.population.getFittest().fitness);
        System.out.println("Genes: ");
        for (int i = 0; i < 20; i++) {///////////////////////////////////////////////////////////////////////
            for(int j=0;j<20;j++){
             System.out.print(demo.population.getFittest().genes[i][j]);
            }
            System.out.println("");
        }

        System.out.println("");

    }

    //Selection
    void selection() {

        //Select the most fittest individual
        fittest = population.getFittest();

        //Select the second most fittest individual
        secondFittest = population.getSecondFittest();
    }

    //Crossover
    void crossover() {
        Random rn = new Random();

        //Select a random crossover point
        for(int i=0;i<20;i++){///////////////////////////////////////////////////////////////////////
          int crossOverPoint = rn.nextInt(population.individuals[0].geneLength);

        //Swap values among parents
            for (int j = 0; j < crossOverPoint; j++) {
                int temp = fittest.genes[i][j];
                fittest.genes[i][j] = secondFittest.genes[i][j];
                secondFittest.genes[i][j] = temp;
        }

        }

    }

    //Mutation
    void mutation() {
        Random rn = new Random();

        //Select a random mutation point
        for(int i=0;i<20;i++) {///////////////////////////////////////////////////////////////////////
            int mutationPoint = rn.nextInt(population.individuals[0].geneLength);

            //Flip values at the mutation point
            if (fittest.genes[i][mutationPoint] == 0) {
                fittest.genes[i][mutationPoint] = 1;
            } else {
                fittest.genes[i][mutationPoint] = 0;
            }

            mutationPoint = rn.nextInt(population.individuals[0].geneLength);

            if (secondFittest.genes[i][mutationPoint] == 0) {
                secondFittest.genes[i][mutationPoint] = 1;
            } else {
                secondFittest.genes[i][mutationPoint] = 0;
            }
        }
    }

    //Get fittest offspring
    Individual getFittestOffspring() {
        if (fittest.fitness > secondFittest.fitness) {
            return fittest;
        }
        return secondFittest;
    }


    //Replace least fittest individual from most fittest offspring
    void addFittestOffspring() {

        //Update fitness values of offspring
        fittest.calcFitness();
        secondFittest.calcFitness();

        //Get index of least fit individual
        int leastFittestIndex = population.getLeastFittestIndex();

        //Replace least fittest individual from most fittest offspring
        population.individuals[leastFittestIndex] = getFittestOffspring();
    }

}


//Individual class
class Individual {

    long fitness = 0;
    public int geneLength = 20;
    int[][] genes = new int[geneLength][geneLength];


    public Individual() {
        Random rn = new Random();

        //Set genes randomly for each individual
        for (int i = 0; i < genes.length; i++) {
            for(int j = 0; j < genes[0].length; j++) {
                genes[i][j] = Math.abs(rn.nextInt() % 2);
            }
        }

        fitness = 0;
    }

    //Calculate fitness
    public void calcFitness() {


        fitness = 0;
        String turnMatrixToString="";
        for(int i=0;i<geneLength;i++){
            for(int j=0;j<geneLength;j++){
                if(genes[i][j]==1){
                    turnMatrixToString+=i+" "+j+",";
                }
            }
        }
//        if(turnMatrixToString!=null){
           fitness=Game.playGame(turnMatrixToString);
//        }
//        else{fitness=Game.playGame("0 1");}




//        for (int i = 0; i < 5; i++) {
//            if (genes[i] == 1) {
//                ++fitness;
//            }
//        }
    }

}

//Population class
class Population {

    int popSize = 100;
    Individual[] individuals = new Individual[popSize];
    int fittest = 0;

    //Initialize population
    public void initializePopulation(int size) {
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual();
        }
    }

    //Get the fittest individual
    public Individual getFittest() {
        int maxFit = Integer.MIN_VALUE;
        int maxFitIndex = 0;
        for (int i = 0; i < individuals.length; i++) {
            if (maxFit <= individuals[i].fitness) {
                maxFit =(int)individuals[i].fitness;
                maxFitIndex = i;
            }
        }
        fittest = (int)individuals[maxFitIndex].fitness;
        return individuals[maxFitIndex];
    }

    //Get the second most fittest individual
    public Individual getSecondFittest() {
        int maxFit1 = 0;
        int maxFit2 = 0;
        for (int i = 0; i < individuals.length; i++) {
            if (individuals[i].fitness > individuals[maxFit1].fitness) {
                maxFit2 = maxFit1;
                maxFit1 = i;
            } else if (individuals[i].fitness > individuals[maxFit2].fitness) {
                maxFit2 = i;
            }
        }
        return individuals[maxFit2];
    }

    //Get index of least fittest individual
    public int getLeastFittestIndex() {
        int minFitVal = Integer.MAX_VALUE;
        int minFitIndex = 0;
        for (int i = 0; i < individuals.length; i++) {
            if (minFitVal >= individuals[i].fitness) {
                minFitVal = (int)individuals[i].fitness;
                minFitIndex = i;
            }
        }
        return minFitIndex;
    }

    //Calculate fitness of each individual
    public void calculateFitness() {

        for (int i = 0; i < individuals.length; i++) {
            individuals[i].calcFitness();
        }
        getFittest();
    }

}



