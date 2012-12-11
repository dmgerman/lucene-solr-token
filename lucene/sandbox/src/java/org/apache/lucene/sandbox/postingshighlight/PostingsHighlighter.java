begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|postingshighlight
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
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|AtomicReader
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
name|AtomicReaderContext
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
name|DocsAndPositionsEnum
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
name|FieldInfo
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
name|IndexReaderContext
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
name|MultiReader
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
name|ReaderUtil
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
name|StoredFieldVisitor
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
name|TermContext
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
name|TermState
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
name|index
operator|.
name|FieldInfo
operator|.
name|IndexOptions
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
name|CollectionStatistics
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
name|TermStatistics
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Simple highlighter that does not analyze fields nor use  * term vectors. Instead it requires   * {@link IndexOptions#DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS}.  *   * This is thread-safe, and can be used across different readers.  *<pre class="prettyprint">  *   // configure field with offsets at index time  *   FieldType offsetsType = new FieldType(TextField.TYPE_STORED);  *   offsetsType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);  *   Field body = new Field("body", "foobar", offsetsType);  *  *   // retrieve highlights at query time   *   PostingsHighlighter highlighter = new PostingsHighlighter("body");  *   Query query = new TermQuery(new Term("body", "highlighting"));  *   TopDocs topDocs = searcher.search(query, n);  *   String highlights[] = highlighter.highlight(query, searcher, topDocs);  *</pre>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PostingsHighlighter
specifier|public
specifier|final
class|class
name|PostingsHighlighter
block|{
comment|// TODO: support highlighting multiple fields at once? someone is bound
comment|// to try to use this in a slow way (invoking over and over for each field), which
comment|// would be horrible.
comment|// TODO: maybe allow re-analysis for tiny fields? currently we require offsets,
comment|// but if the analyzer is really fast and the field is tiny, this might really be
comment|// unnecessary.
comment|/** for rewriting: we don't want slow processing from MTQs */
DECL|field|EMPTY_INDEXREADER
specifier|private
specifier|static
specifier|final
name|IndexReader
name|EMPTY_INDEXREADER
init|=
operator|new
name|MultiReader
argument_list|()
decl_stmt|;
comment|/** Default maximum content size to process. Typically snippets    *  closer to the beginning of the document better summarize its content */
DECL|field|DEFAULT_MAX_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_LENGTH
init|=
literal|10000
decl_stmt|;
comment|// this looks bogus, but its not. we are dealing with characters :)
DECL|field|ceilingBytes
specifier|private
specifier|static
specifier|final
name|BytesRef
name|ceilingBytes
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xff
block|}
argument_list|)
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|floor
specifier|private
specifier|final
name|Term
name|floor
decl_stmt|;
DECL|field|ceiling
specifier|private
specifier|final
name|Term
name|ceiling
decl_stmt|;
DECL|field|maxLength
specifier|private
specifier|final
name|int
name|maxLength
decl_stmt|;
DECL|field|breakIterator
specifier|private
specifier|final
name|BreakIterator
name|breakIterator
decl_stmt|;
DECL|field|scorer
specifier|private
specifier|final
name|PassageScorer
name|scorer
decl_stmt|;
DECL|field|formatter
specifier|private
specifier|final
name|PassageFormatter
name|formatter
decl_stmt|;
DECL|method|PostingsHighlighter
specifier|public
name|PostingsHighlighter
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|DEFAULT_MAX_LENGTH
argument_list|)
expr_stmt|;
block|}
DECL|method|PostingsHighlighter
specifier|public
name|PostingsHighlighter
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|maxLength
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|maxLength
argument_list|,
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
operator|new
name|PassageScorer
argument_list|()
argument_list|,
operator|new
name|PassageFormatter
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|PostingsHighlighter
specifier|public
name|PostingsHighlighter
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|maxLength
parameter_list|,
name|BreakIterator
name|breakIterator
parameter_list|,
name|PassageScorer
name|scorer
parameter_list|,
name|PassageFormatter
name|formatter
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
if|if
condition|(
name|maxLength
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
comment|// two reasons: no overflow problems in BreakIterator.preceding(offset+1),
comment|// our sentinel in the offsets queue uses this value to terminate.
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxLength must be< Integer.MAX_VALUE"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxLength
operator|=
name|maxLength
expr_stmt|;
name|this
operator|.
name|breakIterator
operator|=
name|breakIterator
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
name|floor
operator|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ceiling
operator|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|ceilingBytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link #highlight(Query, IndexSearcher, TopDocs, int) highlight(query, searcher, topDocs, 1)}    */
DECL|method|highlight
specifier|public
name|String
index|[]
name|highlight
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|TopDocs
name|topDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|highlight
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|,
name|topDocs
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|highlight
specifier|public
name|String
index|[]
name|highlight
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|TopDocs
name|topDocs
parameter_list|,
name|int
name|maxPassages
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
specifier|final
name|ScoreDoc
name|scoreDocs
index|[]
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|query
operator|=
name|rewrite
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|SortedSet
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|TreeSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|query
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|terms
operator|=
name|terms
operator|.
name|subSet
argument_list|(
name|floor
argument_list|,
name|ceiling
argument_list|)
expr_stmt|;
comment|// TODO: should we have some reasonable defaults for term pruning? (e.g. stopwords)
name|int
name|docids
index|[]
init|=
operator|new
name|int
index|[
name|scoreDocs
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
name|docids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docids
index|[
name|i
index|]
operator|=
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
expr_stmt|;
block|}
name|IndexReaderContext
name|readerContext
init|=
name|reader
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|readerContext
operator|.
name|leaves
argument_list|()
decl_stmt|;
comment|// sort for sequential io
name|Arrays
operator|.
name|sort
argument_list|(
name|docids
argument_list|)
expr_stmt|;
comment|// pull stored data
name|LimitedStoredFieldVisitor
name|visitor
init|=
operator|new
name|LimitedStoredFieldVisitor
argument_list|(
name|field
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|String
name|contents
index|[]
init|=
operator|new
name|String
index|[
name|docids
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
name|contents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|reader
operator|.
name|document
argument_list|(
name|docids
index|[
name|i
index|]
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
name|contents
index|[
name|i
index|]
operator|=
name|visitor
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|visitor
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// now pull index stats: TODO: we should probably pull this from the reader instead?
comment|// this could be a distributed call, which is crazy
name|CollectionStatistics
name|collectionStats
init|=
name|searcher
operator|.
name|collectionStatistics
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|TermContext
name|termContexts
index|[]
init|=
operator|new
name|TermContext
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|Term
name|termTexts
index|[]
init|=
operator|new
name|Term
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// needed for seekExact
name|float
name|weights
index|[]
init|=
operator|new
name|float
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|termTexts
index|[
name|upto
index|]
operator|=
name|term
expr_stmt|;
name|TermContext
name|context
init|=
name|TermContext
operator|.
name|build
argument_list|(
name|readerContext
argument_list|,
name|term
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|termContexts
index|[
name|upto
index|]
operator|=
name|context
expr_stmt|;
name|TermStatistics
name|termStats
init|=
name|searcher
operator|.
name|termStatistics
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|weights
index|[
name|upto
index|]
operator|=
name|scorer
operator|.
name|weight
argument_list|(
name|collectionStats
argument_list|,
name|termStats
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
comment|// TODO: should we instead score all the documents term-at-a-time here?
comment|// the i/o would be better, but more transient ram
block|}
name|BreakIterator
name|bi
init|=
operator|(
name|BreakIterator
operator|)
name|breakIterator
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|highlights
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// reuse in the real sense... for docs in same segment we just advance our old enum
name|DocsAndPositionsEnum
name|postings
index|[]
init|=
literal|null
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|int
name|lastLeaf
init|=
operator|-
literal|1
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
name|docids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|content
init|=
name|contents
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|content
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
comment|// nothing to do
block|}
name|bi
operator|.
name|setText
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|docids
index|[
name|i
index|]
decl_stmt|;
name|int
name|leaf
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
name|AtomicReaderContext
name|subContext
init|=
name|leaves
operator|.
name|get
argument_list|(
name|leaf
argument_list|)
decl_stmt|;
name|AtomicReader
name|r
init|=
name|subContext
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|t
init|=
name|r
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
continue|continue;
comment|// nothing to do
block|}
if|if
condition|(
name|leaf
operator|!=
name|lastLeaf
condition|)
block|{
name|termsEnum
operator|=
name|t
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|postings
operator|=
operator|new
name|DocsAndPositionsEnum
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
block|}
name|Passage
name|passages
index|[]
init|=
name|highlightDoc
argument_list|(
name|termTexts
argument_list|,
name|termContexts
argument_list|,
name|subContext
operator|.
name|ord
argument_list|,
name|weights
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|,
name|bi
argument_list|,
name|doc
operator|-
name|subContext
operator|.
name|docBase
argument_list|,
name|termsEnum
argument_list|,
name|postings
argument_list|,
name|maxPassages
argument_list|)
decl_stmt|;
if|if
condition|(
name|passages
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// otherwise a null snippet
name|highlights
operator|.
name|put
argument_list|(
name|doc
argument_list|,
name|formatter
operator|.
name|format
argument_list|(
name|passages
argument_list|,
name|content
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastLeaf
operator|=
name|leaf
expr_stmt|;
block|}
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|scoreDocs
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
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|highlights
operator|.
name|get
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|// algorithm: treat sentence snippets as miniature documents
comment|// we can intersect these with the postings lists via BreakIterator.preceding(offset),s
comment|// score each sentence as norm(sentenceStartOffset) * sum(weight * tf(freq))
DECL|method|highlightDoc
specifier|private
name|Passage
index|[]
name|highlightDoc
parameter_list|(
name|Term
name|termTexts
index|[]
parameter_list|,
name|TermContext
index|[]
name|terms
parameter_list|,
name|int
name|ord
parameter_list|,
name|float
index|[]
name|weights
parameter_list|,
name|int
name|contentLength
parameter_list|,
name|BreakIterator
name|bi
parameter_list|,
name|int
name|doc
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|,
name|DocsAndPositionsEnum
index|[]
name|postings
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|PriorityQueue
argument_list|<
name|OffsetsEnum
argument_list|>
name|pq
init|=
operator|new
name|PriorityQueue
argument_list|<
name|OffsetsEnum
argument_list|>
argument_list|()
decl_stmt|;
comment|// initialize postings
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
name|DocsAndPositionsEnum
name|de
init|=
name|postings
index|[
name|i
index|]
decl_stmt|;
name|int
name|pDoc
decl_stmt|;
if|if
condition|(
name|de
operator|==
name|EMPTY
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|de
operator|==
literal|null
condition|)
block|{
name|postings
index|[
name|i
index|]
operator|=
name|EMPTY
expr_stmt|;
comment|// initially
name|TermState
name|ts
init|=
name|terms
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|ord
argument_list|)
decl_stmt|;
if|if
condition|(
name|ts
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|termTexts
index|[
name|i
index|]
operator|.
name|bytes
argument_list|()
argument_list|,
name|ts
argument_list|)
expr_stmt|;
name|DocsAndPositionsEnum
name|de2
init|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|DocsAndPositionsEnum
operator|.
name|FLAG_OFFSETS
argument_list|)
decl_stmt|;
if|if
condition|(
name|de2
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
else|else
block|{
name|de
operator|=
name|postings
index|[
name|i
index|]
operator|=
name|de2
expr_stmt|;
block|}
name|pDoc
operator|=
name|de
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pDoc
operator|=
name|de
operator|.
name|docID
argument_list|()
expr_stmt|;
if|if
condition|(
name|pDoc
operator|<
name|doc
condition|)
block|{
name|pDoc
operator|=
name|de
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|doc
operator|==
name|pDoc
condition|)
block|{
name|de
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|OffsetsEnum
argument_list|(
name|de
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|pq
operator|.
name|add
argument_list|(
operator|new
name|OffsetsEnum
argument_list|(
name|EMPTY
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
comment|// a sentinel for termination
name|PriorityQueue
argument_list|<
name|Passage
argument_list|>
name|passageQueue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|Passage
argument_list|>
argument_list|(
name|n
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Passage
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Passage
name|left
parameter_list|,
name|Passage
name|right
parameter_list|)
block|{
if|if
condition|(
name|right
operator|.
name|score
operator|==
name|left
operator|.
name|score
condition|)
block|{
return|return
name|right
operator|.
name|startOffset
operator|-
name|left
operator|.
name|endOffset
return|;
block|}
else|else
block|{
return|return
name|right
operator|.
name|score
operator|>
name|left
operator|.
name|score
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|Passage
name|current
init|=
operator|new
name|Passage
argument_list|()
decl_stmt|;
name|OffsetsEnum
name|off
decl_stmt|;
while|while
condition|(
operator|(
name|off
operator|=
name|pq
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DocsAndPositionsEnum
name|dp
init|=
name|off
operator|.
name|dp
decl_stmt|;
name|int
name|start
init|=
name|dp
operator|.
name|startOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field '"
operator|+
name|field
operator|+
literal|"' was indexed without offsets, cannot highlight"
argument_list|)
throw|;
block|}
name|int
name|end
init|=
name|dp
operator|.
name|endOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|>
name|current
operator|.
name|endOffset
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|startOffset
operator|>=
literal|0
condition|)
block|{
comment|// finalize current
name|current
operator|.
name|score
operator|*=
name|scorer
operator|.
name|norm
argument_list|(
name|current
operator|.
name|startOffset
argument_list|)
expr_stmt|;
comment|// new sentence: first add 'current' to queue
if|if
condition|(
name|passageQueue
operator|.
name|size
argument_list|()
operator|==
name|n
operator|&&
name|current
operator|.
name|score
operator|<
name|passageQueue
operator|.
name|peek
argument_list|()
operator|.
name|score
condition|)
block|{
name|current
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// can't compete, just reset it
block|}
else|else
block|{
name|passageQueue
operator|.
name|offer
argument_list|(
name|current
argument_list|)
expr_stmt|;
if|if
condition|(
name|passageQueue
operator|.
name|size
argument_list|()
operator|>
name|n
condition|)
block|{
name|current
operator|=
name|passageQueue
operator|.
name|poll
argument_list|()
expr_stmt|;
name|current
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
operator|new
name|Passage
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// if we exceed limit, we are done
if|if
condition|(
name|start
operator|>=
name|contentLength
condition|)
block|{
name|Passage
name|passages
index|[]
init|=
operator|new
name|Passage
index|[
name|passageQueue
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|passageQueue
operator|.
name|toArray
argument_list|(
name|passages
argument_list|)
expr_stmt|;
comment|// sort in ascending order
name|Arrays
operator|.
name|sort
argument_list|(
name|passages
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Passage
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Passage
name|left
parameter_list|,
name|Passage
name|right
parameter_list|)
block|{
return|return
name|left
operator|.
name|startOffset
operator|-
name|right
operator|.
name|startOffset
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|passages
return|;
block|}
comment|// advance breakiterator
assert|assert
name|BreakIterator
operator|.
name|DONE
operator|<
literal|0
assert|;
name|current
operator|.
name|startOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
name|bi
operator|.
name|preceding
argument_list|(
name|start
operator|+
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|current
operator|.
name|endOffset
operator|=
name|Math
operator|.
name|min
argument_list|(
name|bi
operator|.
name|next
argument_list|()
argument_list|,
name|contentLength
argument_list|)
expr_stmt|;
block|}
name|int
name|tf
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|tf
operator|++
expr_stmt|;
name|current
operator|.
name|addMatch
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|termTexts
index|[
name|off
operator|.
name|id
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|off
operator|.
name|pos
operator|==
name|dp
operator|.
name|freq
argument_list|()
condition|)
block|{
break|break;
comment|// removed from pq
block|}
else|else
block|{
name|off
operator|.
name|pos
operator|++
expr_stmt|;
name|dp
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|start
operator|=
name|dp
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|end
operator|=
name|dp
operator|.
name|endOffset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|start
operator|>=
name|current
operator|.
name|endOffset
condition|)
block|{
name|pq
operator|.
name|offer
argument_list|(
name|off
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|current
operator|.
name|score
operator|+=
name|weights
index|[
name|off
operator|.
name|id
index|]
operator|*
name|scorer
operator|.
name|tf
argument_list|(
name|tf
argument_list|,
name|current
operator|.
name|endOffset
operator|-
name|current
operator|.
name|startOffset
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Passage
index|[
literal|0
index|]
return|;
block|}
DECL|class|OffsetsEnum
specifier|private
specifier|static
class|class
name|OffsetsEnum
implements|implements
name|Comparable
argument_list|<
name|OffsetsEnum
argument_list|>
block|{
DECL|field|dp
name|DocsAndPositionsEnum
name|dp
decl_stmt|;
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|method|OffsetsEnum
name|OffsetsEnum
parameter_list|(
name|DocsAndPositionsEnum
name|dp
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dp
operator|=
name|dp
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|pos
operator|=
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|OffsetsEnum
name|other
parameter_list|)
block|{
try|try
block|{
name|int
name|off
init|=
name|dp
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|otherOff
init|=
name|other
operator|.
name|dp
operator|.
name|startOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|off
operator|==
name|otherOff
condition|)
block|{
return|return
name|id
operator|-
name|other
operator|.
name|id
return|;
block|}
else|else
block|{
return|return
name|Long
operator|.
name|signum
argument_list|(
operator|(
operator|(
name|long
operator|)
name|off
operator|)
operator|-
name|otherOff
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|DocsAndPositionsEnum
name|EMPTY
init|=
operator|new
name|DocsAndPositionsEnum
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
block|}
decl_stmt|;
comment|/**     * we rewrite against an empty indexreader: as we don't want things like    * rangeQueries that don't summarize the document    */
DECL|method|rewrite
specifier|private
specifier|static
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|original
decl_stmt|;
for|for
control|(
name|Query
name|rewrittenQuery
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|EMPTY_INDEXREADER
argument_list|)
init|;
name|rewrittenQuery
operator|!=
name|query
condition|;
name|rewrittenQuery
operator|=
name|query
operator|.
name|rewrite
argument_list|(
name|EMPTY_INDEXREADER
argument_list|)
control|)
block|{
name|query
operator|=
name|rewrittenQuery
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|class|LimitedStoredFieldVisitor
specifier|private
specifier|static
class|class
name|LimitedStoredFieldVisitor
extends|extends
name|StoredFieldVisitor
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|maxLength
specifier|private
specifier|final
name|int
name|maxLength
decl_stmt|;
DECL|field|builder
specifier|private
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|method|LimitedStoredFieldVisitor
specifier|public
name|LimitedStoredFieldVisitor
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|maxLength
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|maxLength
operator|=
name|maxLength
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stringField
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// for the offset gap, TODO: make this configurable
block|}
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|+
name|value
operator|.
name|length
argument_list|()
operator|>
name|maxLength
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|maxLength
operator|-
name|builder
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|needsField
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|>
name|maxLength
condition|)
block|{
return|return
name|Status
operator|.
name|STOP
return|;
block|}
return|return
name|Status
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|Status
operator|.
name|NO
return|;
block|}
block|}
DECL|method|getValue
name|String
name|getValue
parameter_list|()
block|{
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

