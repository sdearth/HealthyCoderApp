package com.healthycoderapp;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class BMICalculatorTest {
	private String environment = "prod";
	
	@BeforeAll
	static void beforeAll() {
		System.out.println("Before all Unit Tests");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("After all unit tests");
	}
	
	@Nested
	class IsDietRecommendedTests {

		@ParameterizedTest(name = "weight={0}, height={1}")
		//@ValueSource(doubles = {89.0, 95.0, 110.0})
		//@CsvSource(value = {"89.0, 1.72", "95.0, 1.75", "110.0, 1.78"})
		@CsvFileSource(resources="/diet-recommended-input-data.csv", numLinesToSkip = 1)
		void shouldReturnTrueWhenDietRecommended(Double coderWeight, Double coderHeight) {
			//given
			double weight = coderWeight;
			double height = coderHeight;
			
			//when
			boolean recommended = BMICalculator.isDietRecommended(weight, height);
			
			//then
			assertTrue(recommended);
		}

		@Test
		void shouldReturnFalseWhenDietNotRecommended() {
			//given
			double weight = 50.0;
			double height = 1.92;
			
			//when
			boolean recommended = BMICalculator.isDietRecommended(weight, height);
			
			//then
			assertFalse(recommended);
		}
		
		@Test
		void shouldReturnThrowArithmeticExceptionWhenHeightZero() {
			//given
			double weight = 50.0;
			double height = 0.0;
			
			//when
			Executable executable = () -> BMICalculator.isDietRecommended(weight, height);
			
			//then
			assertThrows(ArithmeticException.class, executable);
		}
	}

	
	@Nested
	class FindCoderWithWorstBMITests {
		@Test
		@DisplayName(">>> sample method with displayname")
		@Disabled
		void shouldReturnCoderWithWorstBMIWhenCoderListNotEmpty() {
			//given
			List<Coder> coders = new ArrayList<>();
			coders.add(new Coder(1.80, 60.0));
			coders.add(new Coder(1.82, 98.0));
			coders.add(new Coder(1.82, 64.7));
			
			//when
			Coder coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);
			
			//then
			assertAll( 
				() -> assertEquals(1.82, coderWorstBMI.getHeight()),
				() -> assertEquals(98.0, coderWorstBMI.getWeight())
			);
		}
		
		@Test
		@DisabledOnOs(OS.MAC)
		void shouldReturnNullWorstBMIWhenCoderListEmpty() {
			//given
			List<Coder> coders = new ArrayList<>();
			
			//when
			Coder coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);
			
			//then
			assertNull(coderWorstBMI);
		}

		
		@Test 
		void shouldReturnCoderWithWorstBMIIn1MsWhenCoderListHas10000Elements() {
			//given
			assumeTrue(environment.equals("prod"));
			List<Coder> coders = new ArrayList<>();
			for (int i=0; i < 10000; i++) {
				coders.add(new Coder(1.0 + i, 10.0 + i));
			}
			
			//when
			Executable executable = () -> BMICalculator.findCoderWithWorstBMI(coders);
			
			//then
			assertTimeout(Duration.ofMillis(500), executable);
		}
	}
	
	@Nested
	class GetBMIScoresTest {
		
		@Test
		void shouldReturnCorrectBMIScoreArrayWhenCoderListNotEmpty() {
			//given
			List<Coder> coders = new ArrayList<>();
			coders.add(new Coder(1.80, 60.0));
			coders.add(new Coder(1.82, 98.0));
			coders.add(new Coder(1.82, 64.7));
			
			double[] expected = {18.52, 29.59, 19.53};
			
			//when
			double[] bmiScores = BMICalculator.getBMIScores(coders);

			//then
			assertArrayEquals(expected, bmiScores);
		}		
	}
	

}
