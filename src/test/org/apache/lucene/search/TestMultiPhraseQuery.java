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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * This class tests the MultiPhraseQuery class.  *  * @author Otis Gospodnetic, Daniel Naber  * @version $Id$  */
end_comment

begin_class
DECL|class|TestMultiPhraseQuery
specifier|public
class|class
name|TestMultiPhraseQuery
extends|extends
name|TestCase
block|{
DECL|method|TestMultiPhraseQuery
specifier|public
name|TestMultiPhraseQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      *      */
DECL|method|testPhrasePrefix
specifier|public
name|void
name|testPhrasePrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
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
name|indexStore
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Document
name|doc1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc5
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc6
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry pie"
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
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry strudel"
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
name|doc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry pizza"
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
name|doc4
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry chewing gum"
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
name|doc5
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"bluebird pizza"
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
name|doc6
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"piccadilly circus"
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
name|doc1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc2
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
name|addDocument
argument_list|(
name|doc4
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc5
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc6
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
name|indexStore
argument_list|)
decl_stmt|;
comment|// search for "blueberry pi*":
name|MultiPhraseQuery
name|query1
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
comment|// search for "strawberry pi*":
name|MultiPhraseQuery
name|query2
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|query1
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry"
argument_list|)
argument_list|)
expr_stmt|;
name|query2
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"strawberry"
argument_list|)
argument_list|)
expr_stmt|;
name|LinkedList
name|termsWithPrefix
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
comment|// this TermEnum gives "piccadilly", "pie" and "pizza".
name|String
name|prefix
init|=
literal|"pi"
decl_stmt|;
name|TermEnum
name|te
init|=
name|ir
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
name|prefix
operator|+
literal|"*"
argument_list|)
argument_list|)
decl_stmt|;
do|do
block|{
if|if
condition|(
name|te
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|termsWithPrefix
operator|.
name|add
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
condition|)
do|;
name|query1
operator|.
name|add
argument_list|(
operator|(
name|Term
index|[]
operator|)
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"body:\"blueberry (piccadilly pie pizza)\""
argument_list|,
name|query1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|query2
operator|.
name|add
argument_list|(
operator|(
name|Term
index|[]
operator|)
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"body:\"strawberry (piccadilly pie pizza)\""
argument_list|,
name|query2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Hits
name|result
decl_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// search for "blue* pizza":
name|MultiPhraseQuery
name|query3
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|termsWithPrefix
operator|.
name|clear
argument_list|()
expr_stmt|;
name|prefix
operator|=
literal|"blue"
expr_stmt|;
name|te
operator|=
name|ir
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
name|prefix
operator|+
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
do|do
block|{
if|if
condition|(
name|te
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|termsWithPrefix
operator|.
name|add
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
condition|)
do|;
name|query3
operator|.
name|add
argument_list|(
operator|(
name|Term
index|[]
operator|)
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|query3
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"pizza"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// blueberry pizza, bluebird pizza
name|assertEquals
argument_list|(
literal|"body:\"(blueberry bluebird) pizza\""
argument_list|,
name|query3
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|MultiPhraseQuery
name|query4
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
try|try
block|{
name|query4
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|query4
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field2"
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// okay, all terms must belong to the same field
block|}
block|}
block|}
end_class

end_unit

