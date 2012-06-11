begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ja.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|dict
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ja
operator|.
name|util
operator|.
name|ToStringUtil
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
name|IntsRef
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
name|UnicodeUtil
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|IntsRefFSTEnum
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
name|fst
operator|.
name|IntsRefFSTEnum
operator|.
name|InputOutput
import|;
end_import

begin_class
DECL|class|TestTokenInfoDictionary
specifier|public
class|class
name|TestTokenInfoDictionary
extends|extends
name|LuceneTestCase
block|{
comment|/** enumerates the entire FST/lookup data and just does basic sanity checks */
DECL|method|testEnumerateAll
specifier|public
name|void
name|testEnumerateAll
parameter_list|()
throws|throws
name|Exception
block|{
comment|// just for debugging
name|int
name|numTerms
init|=
literal|0
decl_stmt|;
name|int
name|numWords
init|=
literal|0
decl_stmt|;
name|int
name|lastWordId
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastSourceId
init|=
operator|-
literal|1
decl_stmt|;
name|TokenInfoDictionary
name|tid
init|=
name|TokenInfoDictionary
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|ConnectionCosts
name|matrix
init|=
name|ConnectionCosts
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
init|=
name|tid
operator|.
name|getFST
argument_list|()
operator|.
name|getInternalFST
argument_list|()
decl_stmt|;
name|IntsRefFSTEnum
argument_list|<
name|Long
argument_list|>
name|fstEnum
init|=
operator|new
name|IntsRefFSTEnum
argument_list|<
name|Long
argument_list|>
argument_list|(
name|fst
argument_list|)
decl_stmt|;
name|InputOutput
argument_list|<
name|Long
argument_list|>
name|mapping
decl_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|mapping
operator|=
name|fstEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|numTerms
operator|++
expr_stmt|;
name|IntsRef
name|input
init|=
name|mapping
operator|.
name|input
decl_stmt|;
name|char
name|chars
index|[]
init|=
operator|new
name|char
index|[
name|input
operator|.
name|length
index|]
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
name|chars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|chars
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|input
operator|.
name|ints
index|[
name|input
operator|.
name|offset
operator|+
name|i
index|]
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|UnicodeUtil
operator|.
name|validUTF16String
argument_list|(
operator|new
name|String
argument_list|(
name|chars
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Long
name|output
init|=
name|mapping
operator|.
name|output
decl_stmt|;
name|int
name|sourceId
init|=
name|output
operator|.
name|intValue
argument_list|()
decl_stmt|;
comment|// we walk in order, terms, sourceIds, and wordIds should always be increasing
name|assertTrue
argument_list|(
name|sourceId
operator|>
name|lastSourceId
argument_list|)
expr_stmt|;
name|lastSourceId
operator|=
name|sourceId
expr_stmt|;
name|tid
operator|.
name|lookupWordIds
argument_list|(
name|sourceId
argument_list|,
name|scratch
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
operator|<
name|scratch
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|numWords
operator|++
expr_stmt|;
name|int
name|wordId
init|=
name|scratch
operator|.
name|ints
index|[
name|scratch
operator|.
name|offset
operator|+
name|i
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|wordId
operator|>
name|lastWordId
argument_list|)
expr_stmt|;
name|lastWordId
operator|=
name|wordId
expr_stmt|;
name|String
name|baseForm
init|=
name|tid
operator|.
name|getBaseForm
argument_list|(
name|wordId
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|baseForm
operator|==
literal|null
operator|||
name|UnicodeUtil
operator|.
name|validUTF16String
argument_list|(
name|baseForm
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|inflectionForm
init|=
name|tid
operator|.
name|getInflectionForm
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|inflectionForm
operator|==
literal|null
operator|||
name|UnicodeUtil
operator|.
name|validUTF16String
argument_list|(
name|inflectionForm
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|inflectionForm
operator|!=
literal|null
condition|)
block|{
comment|// check that its actually an ipadic inflection form
name|assertNotNull
argument_list|(
name|ToStringUtil
operator|.
name|getInflectedFormTranslation
argument_list|(
name|inflectionForm
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|inflectionType
init|=
name|tid
operator|.
name|getInflectionType
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|inflectionType
operator|==
literal|null
operator|||
name|UnicodeUtil
operator|.
name|validUTF16String
argument_list|(
name|inflectionType
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|inflectionType
operator|!=
literal|null
condition|)
block|{
comment|// check that its actually an ipadic inflection type
name|assertNotNull
argument_list|(
name|ToStringUtil
operator|.
name|getInflectionTypeTranslation
argument_list|(
name|inflectionType
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|leftId
init|=
name|tid
operator|.
name|getLeftId
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|int
name|rightId
init|=
name|tid
operator|.
name|getRightId
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|matrix
operator|.
name|get
argument_list|(
name|rightId
argument_list|,
name|leftId
argument_list|)
expr_stmt|;
name|tid
operator|.
name|getWordCost
argument_list|(
name|wordId
argument_list|)
expr_stmt|;
name|String
name|pos
init|=
name|tid
operator|.
name|getPartOfSpeech
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UnicodeUtil
operator|.
name|validUTF16String
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that its actually an ipadic pos tag
name|assertNotNull
argument_list|(
name|ToStringUtil
operator|.
name|getPOSTranslation
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|pronunciation
init|=
name|tid
operator|.
name|getPronunciation
argument_list|(
name|wordId
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pronunciation
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UnicodeUtil
operator|.
name|validUTF16String
argument_list|(
name|pronunciation
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|reading
init|=
name|tid
operator|.
name|getReading
argument_list|(
name|wordId
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reading
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UnicodeUtil
operator|.
name|validUTF16String
argument_list|(
name|reading
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"checked "
operator|+
name|numTerms
operator|+
literal|" terms, "
operator|+
name|numWords
operator|+
literal|" words."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

