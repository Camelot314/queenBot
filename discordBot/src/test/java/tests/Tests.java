package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import discordBot.Queen;
import discordBot.ServerCustomCommands;


class Tests {

	@Test
	void test() {
		char test = '\u2014';
		assertTrue( test == '—');
		assertTrue("i\u2014".equals("i—"));
	}
	
	@Test
	void longTest() {
		ArrayList<Long> test = new ArrayList<>();
		test.add(0L);
		test.add(5L);
		assertTrue(test.size() == 2);
		test.remove((Long) 0L);
		assertTrue(test.size() == 1);
		
		assertTrue((long) test.get(0) == 5L);
	}
	
	@Test
	void serializingCustoms() {
		Queen queen = new Queen();
		ServerCustomCommands custom1 = new ServerCustomCommands(0L);
		ServerCustomCommands custom2 = new ServerCustomCommands(1L);
		
		assertTrue(custom1.addResponse("hi", "there", queen));
		assertTrue(custom1.addResponse("hello there", "general Kenobi", queen));
		assertTrue(custom2.addResponse("test", "test", queen));
		assertTrue(custom2.addResponse("hi", "there", queen));
		
		ArrayList<ServerCustomCommands> list = new ArrayList<>();
		
		list.add(custom1);
		list.add(custom2);
		
//		ServerCustomsPacker packer = new ServerCustomsPacker(list);
		
		try {
			FileOutputStream fileOutput = new FileOutputStream("savedCustoms/testData");
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOutput);
			objectOut.writeObject(list);
			objectOut.flush();
			objectOut.close();
			fileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail("could not write");
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	void deSerialziationCustoms() {
		ArrayList<ServerCustomCommands> list = new ArrayList<>();
		
		try {
			FileInputStream fileIn = new FileInputStream("savedCustoms/testData");
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			list = (ArrayList<ServerCustomCommands>) objectIn.readObject();
			objectIn.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail("could not make object");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("could not find class");
		} catch (ClassCastException e) {
			e.printStackTrace();
			fail("the class type was different");
		}
		for (ServerCustomCommands customs : list) {
			System.out.println(customs);
		}
	}
	
//	@Test
//	void search() {
//		Queen queen = new Queen();
//		Response custom = new Response(".");
//		int index = Collections.binarySearch(queen.responses, custom);
//		for (Response response : queen.responses) {
//			System.out.println(response.getCommand());
//		}
//		assertTrue(index >= 0);
//		
//		Response custom2 = new Response("!customs");
//		boolean isTrue = queen.responses.get(queen.responses.size() - 5).equals(custom2);
//		System.out.println("-------");
//		System.out.println( queen.responses.get(queen.responses.size() - 5).getCommand());
//		System.out.println(custom2.getCommand());
//		System.out.println(isTrue);
//		
//		Collections.sort(queen.responses);
//		index = Collections.binarySearch(queen.responses, custom2);
//		System.out.println(index);
//		assertTrue(index >= 0);
//	}
	
	

}
