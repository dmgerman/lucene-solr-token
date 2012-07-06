begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|analysis
operator|.
name|MockAnalyzer
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
name|MockReaderWrapper
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
name|Tokenizer
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
name|CharTermAttribute
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
name|util
operator|.
name|LuceneTestCase
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
name|_TestUtil
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|BasicOperations
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
name|automaton
operator|.
name|CharacterRunAutomaton
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
name|automaton
operator|.
name|State
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
name|automaton
operator|.
name|Transition
import|;
end_import

begin_comment
comment|/**  * Compares MockTokenizer (which is simple with no optimizations) with equivalent   * core tokenizers (that have optimizations like buffering).  *   * Any tests here need to probably consider unicode version of the JRE (it could  * cause false fails).  */
end_comment

begin_class
DECL|class|TestDuelingAnalyzers
specifier|public
class|class
name|TestDuelingAnalyzers
extends|extends
name|LuceneTestCase
block|{
DECL|field|jvmLetter
specifier|private
name|CharacterRunAutomaton
name|jvmLetter
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// build an automaton matching this jvm's letter definition
name|State
name|initial
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
name|State
name|accept
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
name|accept
operator|.
name|setAccept
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
literal|0x10FFFF
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|initial
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|i
argument_list|,
name|i
argument_list|,
name|accept
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Automaton
name|single
init|=
operator|new
name|Automaton
argument_list|(
name|initial
argument_list|)
decl_stmt|;
name|single
operator|.
name|reduce
argument_list|()
expr_stmt|;
name|Automaton
name|repeat
init|=
name|BasicOperations
operator|.
name|repeat
argument_list|(
name|single
argument_list|)
decl_stmt|;
name|jvmLetter
operator|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|repeat
argument_list|)
expr_stmt|;
block|}
DECL|method|testLetterAscii
specifier|public
name|void
name|testLetterAscii
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|Analyzer
name|left
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|jvmLetter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Analyzer
name|right
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|left
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|,
name|right
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// not so useful since its all one token?!
DECL|method|testLetterAsciiHuge
specifier|public
name|void
name|testLetterAsciiHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|maxLength
init|=
literal|8192
decl_stmt|;
comment|// CharTokenizer.IO_BUFFER_SIZE*2
name|MockAnalyzer
name|left
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|jvmLetter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|left
operator|.
name|setMaxTokenLength
argument_list|(
literal|255
argument_list|)
expr_stmt|;
comment|// match CharTokenizer's max token length
name|Analyzer
name|right
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|left
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|,
name|right
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLetterHtmlish
specifier|public
name|void
name|testLetterHtmlish
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|Analyzer
name|left
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|jvmLetter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Analyzer
name|right
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomHtmlishString
argument_list|(
name|random
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|left
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|,
name|right
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLetterHtmlishHuge
specifier|public
name|void
name|testLetterHtmlishHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|maxLength
init|=
literal|1024
decl_stmt|;
comment|// this is number of elements, not chars!
name|MockAnalyzer
name|left
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|jvmLetter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|left
operator|.
name|setMaxTokenLength
argument_list|(
literal|255
argument_list|)
expr_stmt|;
comment|// match CharTokenizer's max token length
name|Analyzer
name|right
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomHtmlishString
argument_list|(
name|random
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|left
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|,
name|right
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLetterUnicode
specifier|public
name|void
name|testLetterUnicode
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|Analyzer
name|left
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|jvmLetter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Analyzer
name|right
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|left
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|,
name|right
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLetterUnicodeHuge
specifier|public
name|void
name|testLetterUnicodeHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|maxLength
init|=
literal|4300
decl_stmt|;
comment|// CharTokenizer.IO_BUFFER_SIZE + fudge
name|MockAnalyzer
name|left
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|jvmLetter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|left
operator|.
name|setMaxTokenLength
argument_list|(
literal|255
argument_list|)
expr_stmt|;
comment|// match CharTokenizer's max token length
name|Analyzer
name|right
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|left
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|,
name|right
operator|.
name|tokenStream
argument_list|(
literal|"foo"
argument_list|,
name|newStringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we only check a few core attributes here.
comment|// TODO: test other things
DECL|method|assertEquals
specifier|public
name|void
name|assertEquals
parameter_list|(
name|String
name|s
parameter_list|,
name|TokenStream
name|left
parameter_list|,
name|TokenStream
name|right
parameter_list|)
throws|throws
name|Exception
block|{
name|left
operator|.
name|reset
argument_list|()
expr_stmt|;
name|right
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CharTermAttribute
name|leftTerm
init|=
name|left
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|rightTerm
init|=
name|right
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|leftOffset
init|=
name|left
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|rightOffset
init|=
name|right
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|leftPos
init|=
name|left
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|rightPos
init|=
name|right
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|left
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
literal|"wrong number of tokens for input: "
operator|+
name|s
argument_list|,
name|right
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong term text for input: "
operator|+
name|s
argument_list|,
name|leftTerm
operator|.
name|toString
argument_list|()
argument_list|,
name|rightTerm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong position for input: "
operator|+
name|s
argument_list|,
name|leftPos
operator|.
name|getPositionIncrement
argument_list|()
argument_list|,
name|rightPos
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong start offset for input: "
operator|+
name|s
argument_list|,
name|leftOffset
operator|.
name|startOffset
argument_list|()
argument_list|,
name|rightOffset
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong end offset for input: "
operator|+
name|s
argument_list|,
name|leftOffset
operator|.
name|endOffset
argument_list|()
argument_list|,
name|rightOffset
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
name|assertFalse
argument_list|(
literal|"wrong number of tokens for input: "
operator|+
name|s
argument_list|,
name|right
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|left
operator|.
name|end
argument_list|()
expr_stmt|;
name|right
operator|.
name|end
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong final offset for input: "
operator|+
name|s
argument_list|,
name|leftOffset
operator|.
name|endOffset
argument_list|()
argument_list|,
name|rightOffset
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|left
operator|.
name|close
argument_list|()
expr_stmt|;
name|right
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// TODO: maybe push this out to _TestUtil or LuceneTestCase and always use it instead?
DECL|method|newStringReader
specifier|private
specifier|static
name|Reader
name|newStringReader
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|Reader
name|r
init|=
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|r
operator|=
operator|new
name|MockReaderWrapper
argument_list|(
name|random
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

