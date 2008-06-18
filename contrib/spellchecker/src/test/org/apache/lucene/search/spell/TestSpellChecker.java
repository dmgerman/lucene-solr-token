begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|SimpleAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|CorruptIndexException
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
name|IndexReader
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
name|IndexWriter
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
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
name|English
import|;
end_import

begin_comment
comment|/**  * Spell checker test case  *  *  */
end_comment

begin_class
DECL|class|TestSpellChecker
specifier|public
class|class
name|TestSpellChecker
extends|extends
name|TestCase
block|{
DECL|field|spellChecker
specifier|private
name|SpellChecker
name|spellChecker
decl_stmt|;
DECL|field|userindex
DECL|field|spellindex
specifier|private
name|Directory
name|userindex
decl_stmt|,
name|spellindex
decl_stmt|;
DECL|method|setUp
specifier|protected
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
comment|//create a user index
name|userindex
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|userindex
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field1"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field2"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// + word thousand
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// create the spellChecker
name|spellindex
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|spellChecker
operator|=
operator|new
name|SpellChecker
argument_list|(
name|spellindex
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuild
specifier|public
name|void
name|testBuild
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|userindex
argument_list|)
decl_stmt|;
name|spellChecker
operator|.
name|clearIndex
argument_list|()
expr_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
literal|"field1"
argument_list|)
expr_stmt|;
name|int
name|num_field1
init|=
name|this
operator|.
name|numdoc
argument_list|()
decl_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
literal|"field2"
argument_list|)
expr_stmt|;
name|int
name|num_field2
init|=
name|this
operator|.
name|numdoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|num_field2
argument_list|,
name|num_field1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|checkCommonSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|checkLevenshteinSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|setStringDistance
argument_list|(
operator|new
name|JaroWinklerDistance
argument_list|()
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|setAccuracy
argument_list|(
literal|0.8f
argument_list|)
expr_stmt|;
name|checkCommonSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|checkJaroWinklerSuggestions
argument_list|()
expr_stmt|;
block|}
DECL|method|checkCommonSuggestions
specifier|private
name|void
name|checkCommonSuggestions
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fvie"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"five"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|similar
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertFalse
argument_list|(
name|similar
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't suggest a word for itself
block|}
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fiv"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fives"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fie"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
comment|//  test restraint to a field
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|"field1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// there isn't the term thousand in the field field1
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|"field2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// there is the term thousand in the field field2
block|}
DECL|method|checkLevenshteinSuggestions
specifier|private
name|void
name|checkLevenshteinSuggestions
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
comment|// test small word
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fvie"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"five"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"nine"
argument_list|)
expr_stmt|;
comment|// don't suggest a word for itself
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fiv"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"ive"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fives"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fie"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fi"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test restraint to a field
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|"field1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// there isn't the term thousand in the field field1
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|"field2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// there is the term thousand in the field field2
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"onety"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"ninety"
argument_list|)
expr_stmt|;
try|try
block|{
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"threw an NPE, and it shouldn't have"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkJaroWinklerSuggestions
specifier|private
name|void
name|checkJaroWinklerSuggestions
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"onety"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
block|}
DECL|method|addwords
specifier|private
name|void
name|addwords
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|spellChecker
operator|.
name|indexDictionary
argument_list|(
operator|new
name|LuceneDictionary
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
comment|//System.out.println("time to build " + field + ": " + time);
block|}
DECL|method|numdoc
specifier|private
name|int
name|numdoc
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|rs
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|spellindex
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|rs
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|num
operator|!=
literal|0
argument_list|)
expr_stmt|;
comment|//System.out.println("num docs: " + num);
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|num
return|;
block|}
block|}
end_class

end_unit

