begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|*
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
name|*
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
name|index
operator|.
name|Term
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * Tests {@link PhraseQuery}.  *  * @see TestPositionIncrement  * @author Erik Hatcher  */
end_comment

begin_class
DECL|class|TestPhraseQuery
specifier|public
class|class
name|TestPhraseQuery
extends|extends
name|TestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|query
specifier|private
name|PhraseQuery
name|query
decl_stmt|;
DECL|field|directory
specifier|private
name|RAMDirectory
name|directory
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
return|;
block|}
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|100
return|;
block|}
block|}
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
literal|"field"
argument_list|,
literal|"one two three four five"
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
literal|"repeated"
argument_list|,
literal|"this is a repeated field - first part"
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
name|Fieldable
name|repeatedField
init|=
operator|new
name|Field
argument_list|(
literal|"repeated"
argument_list|,
literal|"second part of a repeated field"
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
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|repeatedField
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNotCloseEnough
specifier|public
name|void
name|testNotCloseEnough
parameter_list|()
throws|throws
name|Exception
block|{
name|query
operator|.
name|setSlop
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|testBarelyCloseEnough
specifier|public
name|void
name|testBarelyCloseEnough
parameter_list|()
throws|throws
name|Exception
block|{
name|query
operator|.
name|setSlop
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensures slop of 0 works for exact matches, but not reversed    */
DECL|method|testExact
specifier|public
name|void
name|testExact
parameter_list|()
throws|throws
name|Exception
block|{
comment|// slop is zero by default
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"exact match"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reverse not exact"
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|testSlop1
specifier|public
name|void
name|testSlop1
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Ensures slop of 1 works with terms in order.
name|query
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"in order"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
comment|// Ensures slop of 1 does not work for phrases out of order;
comment|// must be at least 2.
name|query
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reversed, slop not 2 or more"
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|/**    * As long as slop is at least 2, terms can be reversed    */
DECL|method|testOrderDoesntMatter
specifier|public
name|void
name|testOrderDoesntMatter
parameter_list|()
throws|throws
name|Exception
block|{
name|query
operator|.
name|setSlop
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// must be at least two for reverse order match
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"just sloppy enough"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not sloppy enough"
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|/**    * slop is the total number of positional moves allowed    * to line up a phrase    */
DECL|method|testMulipleTerms
specifier|public
name|void
name|testMulipleTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|query
operator|.
name|setSlop
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"two total moves"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|5
argument_list|)
expr_stmt|;
comment|// it takes six moves to match this phrase
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"slop of 5 not close enough"
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"slop of 6 just right"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|testPhraseQueryWithStopAnalyzer
specifier|public
name|void
name|testPhraseQueryWithStopAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|StopAnalyzer
name|stopAnalyzer
init|=
operator|new
name|StopAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|stopAnalyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
literal|"field"
argument_list|,
literal|"the stop words are here"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// valid exact phrase query
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"stop"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"words"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
comment|// currently StopAnalyzer does not leave "holes", so this matches.
name|query
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"words"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"here"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testPhraseQueryInConjunctionScorer
specifier|public
name|void
name|testPhraseQueryInConjunctionScorer
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
literal|"source"
argument_list|,
literal|"marketing info"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
literal|"foobar"
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
literal|"source"
argument_list|,
literal|"marketing info"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|PhraseQuery
name|phraseQuery
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"source"
argument_list|,
literal|"marketing"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"source"
argument_list|,
literal|"info"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|phraseQuery
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|TermQuery
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
name|booleanQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
name|termQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
name|phraseQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|booleanQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|termQuery
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
literal|"map entry woo"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
literal|"woo map entry"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
literal|"map foobarword entry woo"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|termQuery
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"woo"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"map"
argument_list|)
argument_list|)
expr_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"entry"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|termQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|booleanQuery
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
name|termQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
name|phraseQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|booleanQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|booleanQuery
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
name|phraseQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
name|termQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|booleanQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|booleanQuery
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSlopScoring
specifier|public
name|void
name|testSlopScoring
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
literal|"field"
argument_list|,
literal|"foo firstname lastname foo"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"foo firstname xxx lastname foo"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|Document
name|doc3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"foo firstname xxx yyy lastname foo"
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"firstname"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"lastname"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure that those matches where the terms appear closer to
comment|// each other get a higher score:
name|assertEquals
argument_list|(
literal|0.71
argument_list|,
name|hits
operator|.
name|score
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|id
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.44
argument_list|,
name|hits
operator|.
name|score
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.31
argument_list|,
name|hits
operator|.
name|score
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|id
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|testWrappedPhrase
specifier|public
name|void
name|testWrappedPhrase
parameter_list|()
throws|throws
name|IOException
block|{
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"repeated"
argument_list|,
literal|"first"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"repeated"
argument_list|,
literal|"part"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"repeated"
argument_list|,
literal|"second"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"repeated"
argument_list|,
literal|"part"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|99
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

