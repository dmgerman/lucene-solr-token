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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|MultiFields
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
name|RandomIndexWriter
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
name|SingleTermsEnum
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
name|index
operator|.
name|Terms
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
name|TermsEnum
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
name|Rethrow
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
name|TestUtil
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
name|AutomatonTestUtil
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
name|BasicAutomata
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

begin_class
DECL|class|TestAutomatonQuery
specifier|public
class|class
name|TestAutomatonQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|FN
specifier|private
specifier|final
name|String
name|FN
init|=
literal|"field"
decl_stmt|;
annotation|@
name|Override
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|titleField
init|=
name|newTextField
argument_list|(
literal|"title"
argument_list|,
literal|"some title"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|Field
name|field
init|=
name|newTextField
argument_list|(
name|FN
argument_list|,
literal|"this is document one 2345"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|Field
name|footerField
init|=
name|newTextField
argument_list|(
literal|"footer"
argument_list|,
literal|"a footer"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|titleField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|footerField
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"some text from doc two a short piece 5678.91"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"doc three has some different stuff"
operator|+
literal|" with numbers 1234 5678.9 and letter b"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|newTerm
specifier|private
name|Term
name|newTerm
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|FN
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|automatonQueryNrHits
specifier|private
name|int
name|automatonQueryNrHits
parameter_list|(
name|AutomatonQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"TEST: run aq="
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|5
argument_list|)
operator|.
name|totalHits
return|;
block|}
DECL|method|assertAutomatonHits
specifier|private
name|void
name|assertAutomatonHits
parameter_list|(
name|int
name|expected
parameter_list|,
name|Automaton
name|automaton
parameter_list|)
throws|throws
name|IOException
block|{
name|AutomatonQuery
name|query
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"bogus"
argument_list|)
argument_list|,
name|automaton
argument_list|)
decl_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_FILTER_REWRITE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test some very simple automata.    */
DECL|method|testBasicAutomata
specifier|public
name|void
name|testBasicAutomata
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAutomatonHits
argument_list|(
literal|0
argument_list|,
name|BasicAutomata
operator|.
name|makeEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|0
argument_list|,
name|BasicAutomata
operator|.
name|makeEmptyString
argument_list|()
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|2
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyChar
argument_list|()
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|3
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyString
argument_list|()
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|2
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"doc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|BasicAutomata
operator|.
name|makeChar
argument_list|(
literal|'a'
argument_list|)
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|2
argument_list|,
name|BasicAutomata
operator|.
name|makeCharRange
argument_list|(
literal|'a'
argument_list|,
literal|'b'
argument_list|)
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|2
argument_list|,
name|BasicAutomata
operator|.
name|makeInterval
argument_list|(
literal|1233
argument_list|,
literal|2346
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|BasicAutomata
operator|.
name|makeInterval
argument_list|(
literal|0
argument_list|,
literal|2000
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|2
argument_list|,
name|BasicOperations
operator|.
name|union
argument_list|(
name|BasicAutomata
operator|.
name|makeChar
argument_list|(
literal|'a'
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeChar
argument_list|(
literal|'b'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|0
argument_list|,
name|BasicOperations
operator|.
name|intersection
argument_list|(
name|BasicAutomata
operator|.
name|makeChar
argument_list|(
literal|'a'
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeChar
argument_list|(
literal|'b'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|BasicOperations
operator|.
name|minus
argument_list|(
name|BasicAutomata
operator|.
name|makeCharRange
argument_list|(
literal|'a'
argument_list|,
literal|'b'
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeChar
argument_list|(
literal|'a'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that a nondeterministic automaton works correctly. (It should will be    * determinized)    */
DECL|method|testNFA
specifier|public
name|void
name|testNFA
parameter_list|()
throws|throws
name|IOException
block|{
comment|// accept this or three, the union is an NFA (two transitions for 't' from
comment|// initial state)
name|Automaton
name|nfa
init|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"this"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"three"
argument_list|)
argument_list|)
decl_stmt|;
name|assertAutomatonHits
argument_list|(
literal|2
argument_list|,
name|nfa
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|AutomatonQuery
name|a1
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
decl_stmt|;
comment|// reference to a1
name|AutomatonQuery
name|a2
init|=
name|a1
decl_stmt|;
comment|// same as a1 (accepts the same language, same term)
name|AutomatonQuery
name|a3
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// different than a1 (same term, but different language)
name|AutomatonQuery
name|a4
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"different"
argument_list|)
argument_list|)
decl_stmt|;
comment|// different than a1 (different term, same language)
name|AutomatonQuery
name|a5
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"blah"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|a1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|a2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|a3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a1
argument_list|,
name|a3
argument_list|)
expr_stmt|;
comment|// different class
name|AutomatonQuery
name|w1
init|=
operator|new
name|WildcardQuery
argument_list|(
name|newTerm
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
decl_stmt|;
comment|// different class
name|AutomatonQuery
name|w2
init|=
operator|new
name|RegexpQuery
argument_list|(
name|newTerm
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|w1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|w2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|w1
operator|.
name|equals
argument_list|(
name|w2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|a4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|a5
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that rewriting to a single term works as expected, preserves    * MultiTermQuery semantics.    */
DECL|method|testRewriteSingleTerm
specifier|public
name|void
name|testRewriteSingleTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|AutomatonQuery
name|aq
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"bogus"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"piece"
argument_list|)
argument_list|)
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|FN
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|aq
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
operator|instanceof
name|SingleTermsEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|aq
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that rewriting to a prefix query works as expected, preserves    * MultiTermQuery semantics.    */
DECL|method|testRewritePrefix
specifier|public
name|void
name|testRewritePrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|Automaton
name|pfx
init|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"do"
argument_list|)
decl_stmt|;
name|pfx
operator|.
name|expandSingleton
argument_list|()
expr_stmt|;
comment|// expand singleton representation for testing
name|Automaton
name|prefixAutomaton
init|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|pfx
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyString
argument_list|()
argument_list|)
decl_stmt|;
name|AutomatonQuery
name|aq
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"bogus"
argument_list|)
argument_list|,
name|prefixAutomaton
argument_list|)
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|FN
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|aq
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
operator|instanceof
name|PrefixTermsEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|aq
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test handling of the empty language    */
DECL|method|testEmptyOptimization
specifier|public
name|void
name|testEmptyOptimization
parameter_list|()
throws|throws
name|IOException
block|{
name|AutomatonQuery
name|aq
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"bogus"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeEmpty
argument_list|()
argument_list|)
decl_stmt|;
comment|// not yet available: assertTrue(aq.getEnum(searcher.getIndexReader())
comment|// instanceof EmptyTermEnum);
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|FN
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|TermsEnum
operator|.
name|EMPTY
argument_list|,
name|aq
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|aq
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHashCodeWithThreads
specifier|public
name|void
name|testHashCodeWithThreads
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AutomatonQuery
name|queries
index|[]
init|=
operator|new
name|AutomatonQuery
index|[
literal|1000
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
name|queries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|queries
index|[
name|i
index|]
operator|=
operator|new
name|AutomatonQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"bogus"
argument_list|,
literal|"bogus"
argument_list|)
argument_list|,
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|numThreads
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numThreads
index|]
decl_stmt|;
for|for
control|(
name|int
name|threadID
init|=
literal|0
init|;
name|threadID
operator|<
name|numThreads
condition|;
name|threadID
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|startingGun
operator|.
name|await
argument_list|()
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
name|queries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|queries
index|[
name|i
index|]
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Rethrow
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|threads
index|[
name|threadID
index|]
operator|=
name|thread
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

