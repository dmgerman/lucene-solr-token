begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.snowball
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|snowball
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|BaseTokenStreamTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Payload
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|FlagsAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|TypeAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import

begin_class
DECL|class|TestSnowball
specifier|public
class|class
name|TestSnowball
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testEnglish
specifier|public
name|void
name|testEnglish
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|SnowballAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"English"
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"he abhorred accents"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"he"
block|,
literal|"abhor"
block|,
literal|"accent"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopwords
specifier|public
name|void
name|testStopwords
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|SnowballAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"English"
argument_list|,
name|StandardAnalyzer
operator|.
name|STOP_WORDS_SET
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the quick brown fox jumped"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|,
literal|"jump"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test english lowercasing. Test both cases (pre-3.1 and post-3.1) to ensure    * we lowercase I correct for non-Turkish languages in either case.    */
DECL|method|testEnglishLowerCase
specifier|public
name|void
name|testEnglishLowerCase
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|SnowballAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"English"
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"cryogenic"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"cryogen"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"CRYOGENIC"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"cryogen"
block|}
argument_list|)
expr_stmt|;
name|Analyzer
name|b
init|=
operator|new
name|SnowballAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
literal|"English"
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|b
argument_list|,
literal|"cryogenic"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"cryogen"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|b
argument_list|,
literal|"CRYOGENIC"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"cryogen"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test turkish lowercasing    */
DECL|method|testTurkish
specifier|public
name|void
name|testTurkish
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|SnowballAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"Turkish"
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aÄacÄ±"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aÄaÃ§"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"AÄACI"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aÄaÃ§"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test turkish lowercasing (old buggy behavior)    * @deprecated Remove this when support for 3.0 indexes is no longer required    */
DECL|method|testTurkishBWComp
specifier|public
name|void
name|testTurkishBWComp
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|SnowballAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
literal|"Turkish"
argument_list|)
decl_stmt|;
comment|// AÄACI in turkish lowercases to aÄacÄ±, but with lowercase filter aÄaci.
comment|// this fails due to wrong casing, because the stemmer
comment|// will only remove -Ä±, not -i
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aÄacÄ±"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aÄaÃ§"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"AÄACI"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aÄaci"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|SnowballAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"English"
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"he abhorred accents"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"he"
block|,
literal|"abhor"
block|,
literal|"accent"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"she abhorred him"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"she"
block|,
literal|"abhor"
block|,
literal|"him"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFilterTokens
specifier|public
name|void
name|testFilterTokens
parameter_list|()
throws|throws
name|Exception
block|{
name|SnowballFilter
name|filter
init|=
operator|new
name|SnowballFilter
argument_list|(
operator|new
name|TestTokenStream
argument_list|()
argument_list|,
literal|"English"
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TypeAttribute
name|typeAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payloadAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|FlagsAttribute
name|flagsAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|filter
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"accent"
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrd"
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|77
argument_list|,
name|flagsAtt
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Payload
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
argument_list|,
name|payloadAtt
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|TestTokenStream
specifier|private
specifier|final
class|class
name|TestTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|field|payloadAtt
specifier|private
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|field|posIncAtt
specifier|private
name|PositionIncrementAttribute
name|posIncAtt
decl_stmt|;
DECL|field|flagsAtt
specifier|private
name|FlagsAttribute
name|flagsAtt
decl_stmt|;
DECL|method|TestTokenStream
name|TestTokenStream
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|payloadAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|flagsAtt
operator|=
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
literal|"accents"
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
literal|2
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"wrd"
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
literal|77
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

