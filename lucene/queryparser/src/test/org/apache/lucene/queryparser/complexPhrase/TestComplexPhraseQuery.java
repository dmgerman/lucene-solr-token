begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.complexPhrase
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|complexPhrase
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|MockSynonymAnalyzer
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
name|DirectoryReader
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|ScoreDoc
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
name|search
operator|.
name|TopDocs
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

begin_class
DECL|class|TestComplexPhraseQuery
specifier|public
class|class
name|TestComplexPhraseQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|rd
name|Directory
name|rd
decl_stmt|;
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|docsContent
name|DocData
name|docsContent
index|[]
init|=
block|{
operator|new
name|DocData
argument_list|(
literal|"john smith"
argument_list|,
literal|"1"
argument_list|,
literal|"developer"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"johathon smith"
argument_list|,
literal|"2"
argument_list|,
literal|"developer"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"john percival smith"
argument_list|,
literal|"3"
argument_list|,
literal|"designer"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"jackson waits tom"
argument_list|,
literal|"4"
argument_list|,
literal|"project manager"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"johny perkins"
argument_list|,
literal|"5"
argument_list|,
literal|"orders pizza"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"hapax neverson"
argument_list|,
literal|"6"
argument_list|,
literal|"never matches"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"dog cigar"
argument_list|,
literal|"7"
argument_list|,
literal|"just for synonyms"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"dogs don't smoke cigarettes"
argument_list|,
literal|"8"
argument_list|,
literal|"just for synonyms"
argument_list|)
block|,   }
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|defaultFieldName
name|String
name|defaultFieldName
init|=
literal|"name"
decl_stmt|;
DECL|field|inOrder
name|boolean
name|inOrder
init|=
literal|true
decl_stmt|;
DECL|method|testComplexPhrases
specifier|public
name|void
name|testComplexPhrases
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMatches
argument_list|(
literal|"\"john smith\""
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// Simple multi-term still works
name|checkMatches
argument_list|(
literal|"\"j*   smyth~\""
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
comment|// wildcards and fuzzies are OK in
comment|// phrases
name|checkMatches
argument_list|(
literal|"\"(jo* -john)  smith\""
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// boolean logic works
name|checkMatches
argument_list|(
literal|"\"jo*  smith\"~2"
argument_list|,
literal|"1,2,3"
argument_list|)
expr_stmt|;
comment|// position logic works.
name|checkMatches
argument_list|(
literal|"\"jo* [sma TO smZ]\" "
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
comment|// range queries supported
name|checkMatches
argument_list|(
literal|"\"john\""
argument_list|,
literal|"1,3"
argument_list|)
expr_stmt|;
comment|// Simple single-term still works
name|checkMatches
argument_list|(
literal|"\"(john OR johathon)  smith\""
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
comment|// boolean logic with
comment|// brackets works.
name|checkMatches
argument_list|(
literal|"\"(jo* -john) smyth~\""
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// boolean logic with
comment|// brackets works.
comment|// checkMatches("\"john -percival\"", "1"); // not logic doesn't work
comment|// currently :(.
name|checkMatches
argument_list|(
literal|"\"john  nosuchword*\""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// phrases with clauses producing
comment|// empty sets
name|checkBadQuery
argument_list|(
literal|"\"jo*  id:1 smith\""
argument_list|)
expr_stmt|;
comment|// mixing fields in a phrase is bad
name|checkBadQuery
argument_list|(
literal|"\"jo* \"smith\" \""
argument_list|)
expr_stmt|;
comment|// phrases inside phrases is bad
block|}
DECL|method|testSingleTermPhrase
specifier|public
name|void
name|testSingleTermPhrase
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMatches
argument_list|(
literal|"\"joh*\""
argument_list|,
literal|"1,2,3,5"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"joh~\""
argument_list|,
literal|"1,3,5"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"joh*\" \"tom\""
argument_list|,
literal|"1,2,3,4,5"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"+\"j*\" +\"tom\""
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"jo*\" \"[sma TO smZ]\" "
argument_list|,
literal|"1,2,3,5,8"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"+\"j*hn\" +\"sm*h\""
argument_list|,
literal|"1,3"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSynonyms
specifier|public
name|void
name|testSynonyms
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMatches
argument_list|(
literal|"\"dogs\""
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
name|MockSynonymAnalyzer
name|synonym
init|=
operator|new
name|MockSynonymAnalyzer
argument_list|()
decl_stmt|;
name|checkMatches
argument_list|(
literal|"\"dogs\""
argument_list|,
literal|"7,8"
argument_list|,
name|synonym
argument_list|)
expr_stmt|;
comment|// synonym is unidirectional
name|checkMatches
argument_list|(
literal|"\"dog\""
argument_list|,
literal|"7"
argument_list|,
name|synonym
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"dogs cigar*\""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"dog cigar*\""
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"dogs cigar*\""
argument_list|,
literal|"7"
argument_list|,
name|synonym
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"dog cigar*\""
argument_list|,
literal|"7"
argument_list|,
name|synonym
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"dogs cigar*\"~2"
argument_list|,
literal|"7,8"
argument_list|,
name|synonym
argument_list|)
expr_stmt|;
comment|// synonym is unidirectional
name|checkMatches
argument_list|(
literal|"\"dog cigar*\"~2"
argument_list|,
literal|"7"
argument_list|,
name|synonym
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnOrderedProximitySearches
specifier|public
name|void
name|testUnOrderedProximitySearches
parameter_list|()
throws|throws
name|Exception
block|{
name|inOrder
operator|=
literal|true
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"smith jo*\"~2"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// ordered proximity produces empty set
name|inOrder
operator|=
literal|false
expr_stmt|;
name|checkMatches
argument_list|(
literal|"\"smith jo*\"~2"
argument_list|,
literal|"1,2,3"
argument_list|)
expr_stmt|;
comment|// un-ordered proximity
block|}
DECL|method|checkBadQuery
specifier|private
name|void
name|checkBadQuery
parameter_list|(
name|String
name|qString
parameter_list|)
block|{
name|ComplexPhraseQueryParser
name|qp
init|=
operator|new
name|ComplexPhraseQueryParser
argument_list|(
name|defaultFieldName
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setInOrder
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|Throwable
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|qp
operator|.
name|parse
argument_list|(
name|qString
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|checkMatches
specifier|private
name|void
name|checkMatches
parameter_list|(
name|String
name|qString
parameter_list|,
name|String
name|expectedVals
parameter_list|)
throws|throws
name|Exception
block|{
name|checkMatches
argument_list|(
name|qString
argument_list|,
name|expectedVals
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
DECL|method|checkMatches
specifier|private
name|void
name|checkMatches
parameter_list|(
name|String
name|qString
parameter_list|,
name|String
name|expectedVals
parameter_list|,
name|Analyzer
name|anAnalyzer
parameter_list|)
throws|throws
name|Exception
block|{
name|ComplexPhraseQueryParser
name|qp
init|=
operator|new
name|ComplexPhraseQueryParser
argument_list|(
name|defaultFieldName
argument_list|,
name|anAnalyzer
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setInOrder
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
name|qp
operator|.
name|setFuzzyPrefixLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// usually a good idea
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qString
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|expecteds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|String
index|[]
name|vals
init|=
name|expectedVals
operator|.
name|split
argument_list|(
literal|","
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
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|vals
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|expecteds
operator|.
name|add
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|td
operator|.
name|scoreDocs
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
name|sd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|qString
operator|+
literal|"matched doc#"
operator|+
name|id
operator|+
literal|" not expected"
argument_list|,
name|expecteds
operator|.
name|contains
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|expecteds
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|qString
operator|+
literal|" missing some matches "
argument_list|,
literal|0
argument_list|,
name|expecteds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldedQuery
specifier|public
name|void
name|testFieldedQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMatches
argument_list|(
literal|"name:\"john smith\""
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"name:\"j*   smyth~\""
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"role:\"developer\""
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"role:\"p* manager\""
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"role:de*"
argument_list|,
literal|"1,2,3"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"name:\"j* smyth~\"~5"
argument_list|,
literal|"1,2,3"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"role:\"p* manager\" AND name:jack*"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"+role:developer +name:jack*"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkMatches
argument_list|(
literal|"name:\"john smith\"~2 AND role:designer AND id:3"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
block|}
DECL|method|testToStringContainsSlop
specifier|public
name|void
name|testToStringContainsSlop
parameter_list|()
throws|throws
name|Exception
block|{
name|ComplexPhraseQueryParser
name|qp
init|=
operator|new
name|ComplexPhraseQueryParser
argument_list|(
name|defaultFieldName
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|int
name|slop
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|31
argument_list|)
operator|+
literal|1
decl_stmt|;
name|String
name|qString
init|=
literal|"name:\"j* smyth~\"~"
operator|+
name|slop
decl_stmt|;
name|Query
name|query
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qString
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Slop is not shown in toString()"
argument_list|,
name|query
operator|.
name|toString
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"~"
operator|+
name|slop
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|string
init|=
literal|"\"j* smyth~\""
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|string
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Don't show implicit slop of zero"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|,
name|string
argument_list|)
expr_stmt|;
block|}
DECL|method|testHashcodeEquals
specifier|public
name|void
name|testHashcodeEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|ComplexPhraseQueryParser
name|qp
init|=
operator|new
name|ComplexPhraseQueryParser
argument_list|(
name|defaultFieldName
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setInOrder
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|qp
operator|.
name|setFuzzyPrefixLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|String
name|qString
init|=
literal|"\"aaa* bbb*\""
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qString
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qString
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|q
operator|.
name|hashCode
argument_list|()
argument_list|,
name|q2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|qp
operator|.
name|setInOrder
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// SOLR-6011
name|q2
operator|=
name|qp
operator|.
name|parse
argument_list|(
name|qString
argument_list|)
expr_stmt|;
comment|// although the general contract of hashCode can't guarantee different values, if we only change one thing
comment|// about a single query, it normally should result in a different value (and will with the current
comment|// implementation in ComplexPhraseQuery)
name|assertTrue
argument_list|(
name|q
operator|.
name|hashCode
argument_list|()
operator|!=
name|q2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|q
operator|.
name|equals
argument_list|(
name|q2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|q2
operator|.
name|equals
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|analyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|rd
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
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
name|docsContent
operator|.
name|length
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
name|newTextField
argument_list|(
literal|"name"
argument_list|,
name|docsContent
index|[
name|i
index|]
operator|.
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"id"
argument_list|,
name|docsContent
index|[
name|i
index|]
operator|.
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"role"
argument_list|,
name|docsContent
index|[
name|i
index|]
operator|.
name|role
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
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
name|rd
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
DECL|class|DocData
specifier|static
class|class
name|DocData
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|role
name|String
name|role
decl_stmt|;
DECL|method|DocData
specifier|public
name|DocData
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|role
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|role
operator|=
name|role
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

