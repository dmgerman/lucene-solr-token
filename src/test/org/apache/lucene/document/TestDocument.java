begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

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
name|TermQuery
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
name|Searcher
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2003 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_comment
comment|/**  * Tests {@link Document} class.  *  * @author Otis Gospodnetic  * @version $Id$  */
end_comment

begin_class
DECL|class|TestDocument
specifier|public
class|class
name|TestDocument
extends|extends
name|TestCase
block|{
comment|/**      * Tests {@link Document#getValues()} method for a brand new Document      * that has not been indexed yet.      *      * @throws Exception on error      */
DECL|method|testGetValuesForNewDocument
specifier|public
name|void
name|testGetValuesForNewDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|doAssert
argument_list|(
name|makeDocumentWithFields
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests {@link Document#getValues()} method for a Document retrieved from      * an index.      *      * @throws Exception on error      */
DECL|method|testGetValuesForIndexedDocument
specifier|public
name|void
name|testGetValuesForIndexedDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|dir
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
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|makeDocumentWithFields
argument_list|()
argument_list|)
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
name|dir
argument_list|)
decl_stmt|;
comment|// search for something that does exists
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"keyword"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// ensure that queries return expected results without DateFilter first
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
try|try
block|{
name|doAssert
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|makeDocumentWithFields
specifier|private
name|Document
name|makeDocumentWithFields
parameter_list|()
throws|throws
name|IOException
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
name|Keyword
argument_list|(
literal|"keyword"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"keyword"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"text"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"text"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnIndexed
argument_list|(
literal|"unindexed"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnIndexed
argument_list|(
literal|"unindexed"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnStored
argument_list|(
literal|"unstored"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnStored
argument_list|(
literal|"unstored"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|doAssert
specifier|private
name|void
name|doAssert
parameter_list|(
name|Document
name|doc
parameter_list|,
name|boolean
name|fromIndex
parameter_list|)
block|{
name|String
index|[]
name|keywordFieldValues
init|=
name|doc
operator|.
name|getValues
argument_list|(
literal|"keyword"
argument_list|)
decl_stmt|;
name|String
index|[]
name|textFieldValues
init|=
name|doc
operator|.
name|getValues
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|String
index|[]
name|unindexedFieldValues
init|=
name|doc
operator|.
name|getValues
argument_list|(
literal|"unindexed"
argument_list|)
decl_stmt|;
name|String
index|[]
name|unstoredFieldValues
init|=
name|doc
operator|.
name|getValues
argument_list|(
literal|"unstored"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|keywordFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|// this test cannot work for documents retrieved from the index
comment|// since unstored fields will obviously not be returned
if|if
condition|(
operator|!
name|fromIndex
condition|)
block|{
name|assertTrue
argument_list|(
name|unstoredFieldValues
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|keywordFieldValues
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keywordFieldValues
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|textFieldValues
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unindexedFieldValues
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// this test cannot work for documents retrieved from the index
comment|// since unstored fields will obviously not be returned
if|if
condition|(
operator|!
name|fromIndex
condition|)
block|{
name|assertTrue
argument_list|(
name|unstoredFieldValues
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unstoredFieldValues
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

