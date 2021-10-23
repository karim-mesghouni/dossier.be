package com.softline.dossier.be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationTests
{

	public static void main(String[] args)
	{
		SpelExpressionParser parser = new SpelExpressionParser();
		var res = parser.parseRaw("{'a', 'b'}.contains('a')");
		assertThat(res).isEqualTo(true);
	}

	@Test
	void contextLoads()
	{
	}
}
