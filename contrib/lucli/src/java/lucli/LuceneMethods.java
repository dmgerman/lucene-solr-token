begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|lucli
package|package
name|lucli
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
name|ArrayList
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
name|Enumeration
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
name|Iterator
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|jline
operator|.
name|ConsoleReader
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
name|Token
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
name|index
operator|.
name|TermEnum
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
operator|.
name|FieldOption
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
name|queryParser
operator|.
name|MultiFieldQueryParser
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
name|queryParser
operator|.
name|ParseException
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
name|Explanation
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
name|Hits
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
name|Searcher
import|;
end_import

begin_comment
comment|/**  * Various methods that interact with Lucene and provide info about the   * index, search, etc. Parts adapted from Lucene demo.  */
end_comment

begin_class
DECL|class|LuceneMethods
class|class
name|LuceneMethods
block|{
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|field|indexName
specifier|private
name|String
name|indexName
decl_stmt|;
comment|//directory of this index
DECL|field|fieldIterator
specifier|private
name|java
operator|.
name|util
operator|.
name|Iterator
name|fieldIterator
decl_stmt|;
DECL|field|fields
specifier|private
name|List
name|fields
decl_stmt|;
comment|//Fields as a vector
DECL|field|indexedFields
specifier|private
name|List
name|indexedFields
decl_stmt|;
comment|//Fields as a vector
DECL|field|fieldsArray
specifier|private
name|String
name|fieldsArray
index|[]
decl_stmt|;
comment|//Fields as an array
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
comment|//current query string
DECL|field|analyzerClassFQN
specifier|private
name|String
name|analyzerClassFQN
init|=
literal|null
decl_stmt|;
comment|// Analyzer class, if NULL, use default Analyzer
DECL|method|LuceneMethods
specifier|public
name|LuceneMethods
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|indexName
operator|=
name|index
expr_stmt|;
name|message
argument_list|(
literal|"Lucene CLI. Using directory '"
operator|+
name|indexName
operator|+
literal|"'. Type 'help' for instructions."
argument_list|)
expr_stmt|;
block|}
DECL|method|createAnalyzer
specifier|private
name|Analyzer
name|createAnalyzer
parameter_list|()
block|{
if|if
condition|(
name|analyzerClassFQN
operator|==
literal|null
condition|)
return|return
operator|new
name|StandardAnalyzer
argument_list|()
return|;
try|try
block|{
name|Class
name|aClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|analyzerClassFQN
argument_list|)
decl_stmt|;
name|Object
name|obj
init|=
name|aClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|Analyzer
operator|)
condition|)
block|{
name|message
argument_list|(
literal|"Given class is not an Analyzer: "
operator|+
name|analyzerClassFQN
argument_list|)
expr_stmt|;
return|return
operator|new
name|StandardAnalyzer
argument_list|()
return|;
block|}
return|return
operator|(
name|Analyzer
operator|)
name|obj
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|message
argument_list|(
literal|"Unable to use Analyzer "
operator|+
name|analyzerClassFQN
argument_list|)
expr_stmt|;
return|return
operator|new
name|StandardAnalyzer
argument_list|()
return|;
block|}
block|}
DECL|method|info
specifier|public
name|void
name|info
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
name|getFieldInfo
argument_list|()
expr_stmt|;
name|numDocs
operator|=
name|indexReader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|message
argument_list|(
literal|"Index has "
operator|+
name|numDocs
operator|+
literal|" documents "
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"All Fields:"
operator|+
name|fields
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"Indexed Fields:"
operator|+
name|indexedFields
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|IndexReader
operator|.
name|isLocked
argument_list|(
name|indexName
argument_list|)
condition|)
block|{
name|message
argument_list|(
literal|"Index is locked"
argument_list|)
expr_stmt|;
block|}
comment|//IndexReader.getCurrentVersion(indexName);
comment|//System.out.println("Version:" + version);
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|String
name|queryString
parameter_list|,
name|boolean
name|explain
parameter_list|,
name|boolean
name|showTokens
parameter_list|,
name|ConsoleReader
name|cr
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
throws|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ParseException
block|{
name|Hits
name|hits
init|=
name|initSearch
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" total matching documents"
argument_list|)
expr_stmt|;
if|if
condition|(
name|explain
condition|)
block|{
name|query
operator|=
name|explainQuery
argument_list|(
name|queryString
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|HITS_PER_PAGE
init|=
literal|10
decl_stmt|;
name|message
argument_list|(
literal|"--------------------------------------"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|start
init|=
literal|0
init|;
name|start
operator|<
name|hits
operator|.
name|length
argument_list|()
condition|;
name|start
operator|+=
name|HITS_PER_PAGE
control|)
block|{
name|int
name|end
init|=
name|Math
operator|.
name|min
argument_list|(
name|hits
operator|.
name|length
argument_list|()
argument_list|,
name|start
operator|+
name|HITS_PER_PAGE
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
name|start
init|;
name|ii
operator|<
name|end
condition|;
name|ii
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|message
argument_list|(
literal|"---------------- "
operator|+
operator|(
name|ii
operator|+
literal|1
operator|)
operator|+
literal|" score:"
operator|+
name|hits
operator|.
name|score
argument_list|(
name|ii
argument_list|)
operator|+
literal|"---------------------"
argument_list|)
expr_stmt|;
name|printHit
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|showTokens
condition|)
block|{
name|invertDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|explain
condition|)
block|{
name|Explanation
name|exp
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|hits
operator|.
name|id
argument_list|(
name|ii
argument_list|)
argument_list|)
decl_stmt|;
name|message
argument_list|(
literal|"Explanation:"
operator|+
name|exp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|message
argument_list|(
literal|"#################################################"
argument_list|)
expr_stmt|;
if|if
condition|(
name|hits
operator|.
name|length
argument_list|()
operator|>
name|end
condition|)
block|{
comment|// TODO: don't let the input end up in the command line history
name|queryString
operator|=
name|cr
operator|.
name|readLine
argument_list|(
literal|"more (y/n) ? "
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryString
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|queryString
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'n'
condition|)
break|break;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * TODO: Allow user to specify what field(s) to display    */
DECL|method|printHit
specifier|private
name|void
name|printHit
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|fieldsArray
operator|.
name|length
condition|;
name|ii
operator|++
control|)
block|{
name|String
name|currField
init|=
name|fieldsArray
index|[
name|ii
index|]
decl_stmt|;
name|String
index|[]
name|result
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|currField
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|message
argument_list|(
name|currField
operator|+
literal|":"
operator|+
name|result
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|message
argument_list|(
name|currField
operator|+
literal|":<not available>"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//another option is to just do message(doc);
block|}
DECL|method|optimize
specifier|public
name|void
name|optimize
parameter_list|()
throws|throws
name|IOException
block|{
comment|//open the index writer. False: don't create a new one
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexName
argument_list|,
name|createAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|message
argument_list|(
literal|"Starting to optimize index."
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|indexWriter
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|message
argument_list|(
literal|"Done optimizing index. Took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|" msecs"
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|explainQuery
specifier|private
name|Query
name|explainQuery
parameter_list|(
name|String
name|queryString
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
name|createAnalyzer
argument_list|()
decl_stmt|;
name|getFieldInfo
argument_list|()
expr_stmt|;
name|int
name|arraySize
init|=
name|indexedFields
operator|.
name|size
argument_list|()
decl_stmt|;
name|String
name|indexedArray
index|[]
init|=
operator|new
name|String
index|[
name|arraySize
index|]
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|arraySize
condition|;
name|ii
operator|++
control|)
block|{
name|indexedArray
index|[
name|ii
index|]
operator|=
operator|(
name|String
operator|)
name|indexedFields
operator|.
name|get
argument_list|(
name|ii
argument_list|)
expr_stmt|;
block|}
name|MultiFieldQueryParser
name|parser
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|indexedArray
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|queryString
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Searching for: "
operator|+
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|query
operator|)
return|;
block|}
comment|/**    * TODO: Allow user to specify analyzer    */
DECL|method|initSearch
specifier|private
name|Hits
name|initSearch
parameter_list|(
name|String
name|queryString
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
name|createAnalyzer
argument_list|()
decl_stmt|;
name|getFieldInfo
argument_list|()
expr_stmt|;
name|int
name|arraySize
init|=
name|fields
operator|.
name|size
argument_list|()
decl_stmt|;
name|fieldsArray
operator|=
operator|new
name|String
index|[
name|arraySize
index|]
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|arraySize
condition|;
name|ii
operator|++
control|)
block|{
name|fieldsArray
index|[
name|ii
index|]
operator|=
operator|(
name|String
operator|)
name|fields
operator|.
name|get
argument_list|(
name|ii
argument_list|)
expr_stmt|;
block|}
name|MultiFieldQueryParser
name|parser
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|fieldsArray
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|queryString
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Searching for: "
operator|+
name|query
operator|.
name|toString
argument_list|()
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
return|return
operator|(
name|hits
operator|)
return|;
block|}
DECL|method|count
specifier|public
name|void
name|count
parameter_list|(
name|String
name|queryString
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
throws|,
name|ParseException
block|{
name|Hits
name|hits
init|=
name|initSearch
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" total documents"
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|message
specifier|static
specifier|public
name|void
name|message
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldInfo
specifier|private
name|void
name|getFieldInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
name|fields
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|indexedFields
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
comment|//get the list of all field names
name|fieldIterator
operator|=
name|indexReader
operator|.
name|getFieldNames
argument_list|(
name|FieldOption
operator|.
name|ALL
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|fieldIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Object
name|field
init|=
name|fieldIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
operator|!
name|field
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
name|fields
operator|.
name|add
argument_list|(
name|field
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//
comment|//get the list of indexed field names
name|fieldIterator
operator|=
name|indexReader
operator|.
name|getFieldNames
argument_list|(
name|FieldOption
operator|.
name|INDEXED
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|fieldIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Object
name|field
init|=
name|fieldIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
operator|!
name|field
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
name|indexedFields
operator|.
name|add
argument_list|(
name|field
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Copied from DocumentWriter
comment|// Tokenizes the fields of a document into Postings.
DECL|method|invertDocument
specifier|private
name|void
name|invertDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
name|tokenMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxFieldLength
init|=
literal|10000
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|createAnalyzer
argument_list|()
decl_stmt|;
name|Iterator
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|field
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
block|{
comment|// un-tokenized field
name|Reader
name|reader
decl_stmt|;
comment|// find or make Reader
if|if
condition|(
name|field
operator|.
name|readerValue
argument_list|()
operator|!=
literal|null
condition|)
name|reader
operator|=
name|field
operator|.
name|readerValue
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must have either String or Reader value"
argument_list|)
throw|;
name|int
name|position
init|=
literal|0
decl_stmt|;
comment|// Tokenize field and add to postingTable
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|position
operator|+=
operator|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|-
literal|1
operator|)
expr_stmt|;
name|position
operator|++
expr_stmt|;
name|String
name|name
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
name|Integer
name|Count
init|=
operator|(
name|Integer
operator|)
name|tokenMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|Count
operator|==
literal|null
condition|)
block|{
comment|// not in there yet
name|tokenMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//first one
block|}
else|else
block|{
name|int
name|count
init|=
name|Count
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|tokenMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Integer
argument_list|(
name|count
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|position
operator|>
name|maxFieldLength
condition|)
break|break;
block|}
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|Entry
index|[]
name|sortedHash
init|=
name|getSortedMapEntries
argument_list|(
name|tokenMap
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|sortedHash
operator|.
name|length
operator|&&
name|ii
operator|<
literal|10
condition|;
name|ii
operator|++
control|)
block|{
name|Entry
name|currentEntry
init|=
name|sortedHash
index|[
name|ii
index|]
decl_stmt|;
name|message
argument_list|(
operator|(
name|ii
operator|+
literal|1
operator|)
operator|+
literal|":"
operator|+
name|currentEntry
operator|.
name|getKey
argument_list|()
operator|+
literal|" "
operator|+
name|currentEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Provides a list of the top terms of the index.    *    * @param field  - the name of the command or null for all of them.    */
DECL|method|terms
specifier|public
name|void
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeMap
name|termMap
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
name|TermEnum
name|terms
init|=
name|indexReader
operator|.
name|terms
argument_list|()
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
name|Term
name|term
init|=
name|terms
operator|.
name|term
argument_list|()
decl_stmt|;
comment|//message(term.field() + ":" + term.text() + " freq:" + terms.docFreq());
comment|//if we're either not looking by field or we're matching the specific field
if|if
condition|(
operator|(
name|field
operator|==
literal|null
operator|)
operator|||
name|field
operator|.
name|equals
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
condition|)
name|termMap
operator|.
name|put
argument_list|(
name|term
operator|.
name|field
argument_list|()
operator|+
literal|":"
operator|+
name|term
operator|.
name|text
argument_list|()
argument_list|,
operator|new
name|Integer
argument_list|(
operator|(
name|terms
operator|.
name|docFreq
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Iterator
name|termIterator
init|=
name|termMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|termIterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|ii
operator|<
literal|100
condition|;
name|ii
operator|++
control|)
block|{
name|String
name|termDetails
init|=
operator|(
name|String
operator|)
name|termIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Integer
name|termFreq
init|=
operator|(
name|Integer
operator|)
name|termMap
operator|.
name|get
argument_list|(
name|termDetails
argument_list|)
decl_stmt|;
name|message
argument_list|(
name|termDetails
operator|+
literal|": "
operator|+
name|termFreq
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Sort Map values    * @param m the map we're sorting    * from http://developer.java.sun.com/developer/qow/archive/170/index.jsp    */
specifier|public
specifier|static
name|Entry
index|[]
DECL|method|getSortedMapEntries
name|getSortedMapEntries
parameter_list|(
name|Map
name|m
parameter_list|)
block|{
name|Set
name|set
init|=
name|m
operator|.
name|entrySet
argument_list|()
decl_stmt|;
name|Entry
index|[]
name|entries
init|=
operator|(
name|Entry
index|[]
operator|)
name|set
operator|.
name|toArray
argument_list|(
operator|new
name|Entry
index|[
name|set
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|entries
argument_list|,
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|Object
name|v1
init|=
operator|(
operator|(
name|Entry
operator|)
name|o1
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|v2
init|=
operator|(
operator|(
name|Entry
operator|)
name|o2
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|Comparable
operator|)
name|v2
operator|)
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
return|;
comment|//descending order
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|entries
return|;
block|}
DECL|method|analyzer
specifier|public
name|void
name|analyzer
parameter_list|(
name|String
name|word
parameter_list|)
block|{
if|if
condition|(
literal|"current"
operator|.
name|equals
argument_list|(
name|word
argument_list|)
condition|)
block|{
name|String
name|current
init|=
name|analyzerClassFQN
operator|==
literal|null
condition|?
literal|"StandardAnalyzer"
else|:
name|analyzerClassFQN
decl_stmt|;
name|message
argument_list|(
literal|"The currently used Analyzer class is: "
operator|+
name|current
argument_list|)
expr_stmt|;
return|return;
block|}
name|analyzerClassFQN
operator|=
name|word
expr_stmt|;
name|message
argument_list|(
literal|"Switched to Analyzer class "
operator|+
name|analyzerClassFQN
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

