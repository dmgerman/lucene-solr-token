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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|TestTermVectors
specifier|public
class|class
name|TestTermVectors
extends|extends
name|TestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|directory
specifier|private
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|method|TestTermVectors
specifier|public
name|TestTermVectors
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//writer.setUseCompoundFile(true);
comment|//writer.infoStream = System.out;
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
name|Field
operator|.
name|Text
argument_list|(
literal|"field"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
literal|true
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
block|}
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
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{    }
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|searcher
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermVectors
specifier|public
name|void
name|testTermVectors
parameter_list|()
block|{
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"seventy"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
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
literal|100
argument_list|,
name|hits
operator|.
name|length
argument_list|()
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
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TermFreqVector
index|[]
name|vector
init|=
name|searcher
operator|.
name|reader
operator|.
name|getTermFreqVectors
argument_list|(
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|//assertTrue();
block|}
name|TermFreqVector
index|[]
name|vector
init|=
name|searcher
operator|.
name|reader
operator|.
name|getTermFreqVectors
argument_list|(
name|hits
operator|.
name|id
argument_list|(
literal|50
argument_list|)
argument_list|)
decl_stmt|;
comment|//System.out.println("Explain: " + searcher.explain(query, hits.id(50)));
comment|//System.out.println("Vector: " + vector[0].toString());
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTermPositionVectors
specifier|public
name|void
name|testTermPositionVectors
parameter_list|()
block|{
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"fifty"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
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
literal|100
argument_list|,
name|hits
operator|.
name|length
argument_list|()
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
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TermFreqVector
index|[]
name|vector
init|=
name|searcher
operator|.
name|reader
operator|.
name|getTermFreqVectors
argument_list|(
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|//assertTrue();
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testKnownSetOfDocuments
specifier|public
name|void
name|testKnownSetOfDocuments
parameter_list|()
block|{
name|String
name|test1
init|=
literal|"eating chocolate in a computer lab"
decl_stmt|;
comment|//6 terms
name|String
name|test2
init|=
literal|"computer in a computer lab"
decl_stmt|;
comment|//5 terms
name|String
name|test3
init|=
literal|"a chocolate lab grows old"
decl_stmt|;
comment|//5 terms
name|String
name|test4
init|=
literal|"eating chocolate with a chocolate lab in an old chocolate colored computer lab"
decl_stmt|;
comment|//13 terms
name|Map
name|test4Map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"chocolate"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"lab"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"eating"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"computer"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"with"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"colored"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"in"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"an"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"computer"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|test4Map
operator|.
name|put
argument_list|(
literal|"old"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|testDoc1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|setupDoc
argument_list|(
name|testDoc1
argument_list|,
name|test1
argument_list|)
expr_stmt|;
name|Document
name|testDoc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|setupDoc
argument_list|(
name|testDoc2
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|Document
name|testDoc3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|setupDoc
argument_list|(
name|testDoc3
argument_list|,
name|test3
argument_list|)
expr_stmt|;
name|Document
name|testDoc4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|setupDoc
argument_list|(
name|testDoc4
argument_list|,
name|test4
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
try|try
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|testDoc1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|testDoc2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|testDoc3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|testDoc4
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|knownSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|knownSearcher
operator|.
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|knownSearcher
operator|.
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
comment|//System.out.println("Terms: " + termEnum.size() + " Orig Len: " + termArray.length);
name|Similarity
name|sim
init|=
name|knownSearcher
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
while|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
operator|==
literal|true
condition|)
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|termDocs
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|docId
init|=
name|termDocs
operator|.
name|doc
argument_list|()
decl_stmt|;
name|int
name|freq
init|=
name|termDocs
operator|.
name|freq
argument_list|()
decl_stmt|;
comment|//System.out.println("Doc Id: " + docId + " freq " + freq);
name|TermFreqVector
name|vector
init|=
name|knownSearcher
operator|.
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docId
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|float
name|tf
init|=
name|sim
operator|.
name|tf
argument_list|(
name|freq
argument_list|)
decl_stmt|;
name|float
name|idf
init|=
name|sim
operator|.
name|idf
argument_list|(
name|term
argument_list|,
name|knownSearcher
argument_list|)
decl_stmt|;
comment|//float qNorm = sim.queryNorm()
comment|//This is fine since we don't have stop words
name|float
name|lNorm
init|=
name|sim
operator|.
name|lengthNorm
argument_list|(
literal|"field"
argument_list|,
name|vector
operator|.
name|getTerms
argument_list|()
operator|.
name|length
argument_list|)
decl_stmt|;
comment|//float coord = sim.coord()
comment|//System.out.println("TF: " + tf + " IDF: " + idf + " LenNorm: " + lNorm);
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
index|[]
name|vTerms
init|=
name|vector
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|int
index|[]
name|freqs
init|=
name|vector
operator|.
name|getTermFrequencies
argument_list|()
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
name|vTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|term
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
name|vTerms
index|[
name|i
index|]
argument_list|)
operator|==
literal|true
condition|)
block|{
name|assertTrue
argument_list|(
name|freqs
index|[
name|i
index|]
operator|==
name|freq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//System.out.println("--------");
block|}
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"chocolate"
argument_list|)
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|knownSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|//doc 3 should be the first hit b/c it is the shortest match
name|assertTrue
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|hits
operator|.
name|score
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/*System.out.println("Hit 0: " + hits.id(0) + " Score: " + hits.score(0) + " String: " + hits.doc(0).toString());       System.out.println("Explain: " + knownSearcher.explain(query, hits.id(0)));       System.out.println("Hit 1: " + hits.id(1) + " Score: " + hits.score(1) + " String: " + hits.doc(1).toString());       System.out.println("Explain: " + knownSearcher.explain(query, hits.id(1)));       System.out.println("Hit 2: " + hits.id(2) + " Score: " + hits.score(2) + " String: " +  hits.doc(2).toString());       System.out.println("Explain: " + knownSearcher.explain(query, hits.id(2)));*/
name|assertTrue
argument_list|(
name|testDoc3
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testDoc4
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testDoc1
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TermFreqVector
name|vector
init|=
name|knownSearcher
operator|.
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|hits
operator|.
name|id
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Vector: " + vector);
name|String
index|[]
name|terms
init|=
name|vector
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|int
index|[]
name|freqs
init|=
name|vector
operator|.
name|getTermFrequencies
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|!=
literal|null
operator|&&
name|terms
operator|.
name|length
operator|==
literal|10
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|terms
index|[
name|i
index|]
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|int
name|freq
init|=
name|freqs
index|[
name|i
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|test4
operator|.
name|indexOf
argument_list|(
name|term
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Integer
name|freqInt
init|=
operator|(
name|Integer
operator|)
name|test4Map
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|freqInt
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|freqInt
operator|.
name|intValue
argument_list|()
operator|==
name|freq
argument_list|)
expr_stmt|;
block|}
name|knownSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setupDoc
specifier|private
name|void
name|setupDoc
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"field"
argument_list|,
name|text
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("Document: " + doc);
block|}
block|}
end_class

end_unit

