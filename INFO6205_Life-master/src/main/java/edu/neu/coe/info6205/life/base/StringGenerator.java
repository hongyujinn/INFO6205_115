package edu.neu.coe.info6205.life.base;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;

import java.util.stream.IntStream;

import io.jenetics.CharacterChromosome;
import io.jenetics.CharacterGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.StochasticUniversalSelector;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Problem;
import io.jenetics.util.CharSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.5
 */
public class StringGenerator {

    private static final String TARGET_STRING = "jenetics";

    private static final Problem<CharSequence, CharacterGene, Integer> PROBLEM =
            Problem.of(
                    seq -> IntStream.range(0, TARGET_STRING.length())
                            .map(i -> seq.charAt(i) == TARGET_STRING.charAt(i) ? 1 : 0)
                            .sum(),
                    Codec.of(
                            Genotype.of(CharacterChromosome.of(
                                    CharSeq.of("a-z"), TARGET_STRING.length()
                            )),
                            gt -> (CharSequence)gt.getChromosome()
                    )
            );

    public static void main(final String[] args) {
        final Engine<CharacterGene, Integer> engine = Engine.builder(PROBLEM)
                .populationSize(500)
                .survivorsSelector(new StochasticUniversalSelector<>())
                .offspringSelector(new TournamentSelector<>(5))
                .alterers(
                        new Mutator<>(0.1),
                        new SinglePointCrossover<>(0.5))
                .build();

        final Phenotype<CharacterGene, Integer> result = engine.stream()
                .limit(100)
                .collect(toBestPhenotype());

        System.out.println(result);
    }

}