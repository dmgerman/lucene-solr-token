begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|com.relevanz.indyo
package|package
name|com
operator|.
name|relevanz
operator|.
name|indyo
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|com
operator|.
name|relevanz
operator|.
name|indyo
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|IndexWriter
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<p>  * A document is the atomic unit used for indexing purposes. It consists of  * metadata as well as its file contents. File contents are handled by  * {@link ContentHandler}.  *</p>  *<p>  * DocumentHandler creates the {@link org.apache.lucene.document.Document},  * adds fields to it, delegates to {@link ContentHandler} to handle  * file contents.  *</p>  *   * @version $Id$  */
end_comment

begin_class
DECL|class|DocumentHandler
specifier|public
class|class
name|DocumentHandler
block|{
comment|/**      * Field to retrieve all documents.      */
DECL|field|ALL_DOCUMENTS_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ALL_DOCUMENTS_FIELD
init|=
literal|"AllDocuments"
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DocumentHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|isDebugEnabled
specifier|private
specifier|static
name|boolean
name|isDebugEnabled
init|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
comment|/**      * Should parent documents include data of its children?      */
DECL|field|parentEncapsulation
specifier|private
specifier|static
name|boolean
name|parentEncapsulation
init|=
literal|false
decl_stmt|;
comment|/**      * Document object this DocumentHandler is handling.      */
DECL|field|doc
specifier|private
name|Document
name|doc
decl_stmt|;
comment|/**      * Map of metadata for this document. Contains the field:value pair      * to be added to the document.      */
DECL|field|metadata
specifier|private
name|Map
name|metadata
decl_stmt|;
comment|/**      * Map of fields. Contains field:type_of_field pair.      */
DECL|field|customFields
specifier|private
name|Map
name|customFields
decl_stmt|;
comment|/**      * IndexWriter.      */
DECL|field|writer
specifier|private
name|IndexWriter
name|writer
decl_stmt|;
comment|/**      * A collection of documents to be added to the writer.      */
DECL|field|documents
specifier|private
name|List
name|documents
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/**      * Ctor.      *      * @param Map of metadata for this document.      * @param Map of fields.      * @param Writer.      */
DECL|method|DocumentHandler
specifier|public
name|DocumentHandler
parameter_list|(
name|Map
name|metadata
parameter_list|,
name|Map
name|customFields
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|customFields
operator|=
name|customFields
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
comment|/**      * Handles the actual processing of the document.      */
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|String
name|objectid
init|=
operator|(
name|String
operator|)
name|metadata
operator|.
name|get
argument_list|(
name|IndexDataSource
operator|.
name|OBJECT_IDENTIFIER
argument_list|)
decl_stmt|;
if|if
condition|(
name|objectid
operator|==
literal|null
condition|)
return|return;
name|doc
operator|=
name|createDocument
argument_list|()
expr_stmt|;
name|addMapToDoc
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|addNestedDataSource
argument_list|(
name|metadata
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
name|ALL_DOCUMENTS_FIELD
argument_list|,
name|ALL_DOCUMENTS_FIELD
argument_list|)
argument_list|)
expr_stmt|;
comment|//documents.add(doc);
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|addToWriter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|documents
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDocuments
specifier|private
name|List
name|getDocuments
parameter_list|()
block|{
return|return
name|documents
return|;
block|}
DECL|method|createDocument
specifier|private
name|Document
name|createDocument
parameter_list|()
block|{
return|return
operator|new
name|Document
argument_list|()
return|;
block|}
comment|/**      * Add the contents of a Map to a document.      *      * @param Map to add.      */
DECL|method|addMapToDoc
specifier|private
name|void
name|addMapToDoc
parameter_list|(
name|Map
name|map
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|it
init|=
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|field
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|map
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|String
name|type
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|customFields
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
operator|(
name|String
operator|)
name|customFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|addFieldToDoc
argument_list|(
name|type
argument_list|,
name|field
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Reader
condition|)
block|{
name|addFieldToDoc
argument_list|(
name|field
argument_list|,
operator|(
name|Reader
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Add nested datasources.      *      * @param Map which contains the nested datasources.      */
DECL|method|addNestedDataSource
specifier|private
name|void
name|addNestedDataSource
parameter_list|(
name|Map
name|map
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
name|o
init|=
name|map
operator|.
name|get
argument_list|(
name|IndexDataSource
operator|.
name|NESTED_DATASOURCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|o
operator|instanceof
name|IndexDataSource
condition|)
block|{
name|IndexDataSource
name|ds
init|=
operator|(
name|IndexDataSource
operator|)
name|o
decl_stmt|;
name|addDataSource
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|List
name|nestedDataSource
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|n
init|=
name|nestedDataSource
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|IndexDataSource
name|ds
init|=
operator|(
name|IndexDataSource
operator|)
name|nestedDataSource
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|addDataSource
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|IndexDataSource
index|[]
condition|)
block|{
name|IndexDataSource
index|[]
name|nestedDataSource
init|=
operator|(
name|IndexDataSource
index|[]
operator|)
name|o
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|n
init|=
name|nestedDataSource
operator|.
name|length
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|IndexDataSource
name|ds
init|=
operator|(
name|IndexDataSource
operator|)
name|nestedDataSource
index|[
name|i
index|]
decl_stmt|;
name|addDataSource
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown object found as nested datasource:"
operator|+
name|o
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Datasources are basically a collection of data maps to be indexed.      * addMapToDoc is invoked for each map.      *      * @param Datasource to add.      */
DECL|method|addDataSource
specifier|private
name|void
name|addDataSource
parameter_list|(
name|IndexDataSource
name|ds
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
index|[]
name|data
init|=
name|ds
operator|.
name|getData
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Map
name|map
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|IndexDataSource
operator|.
name|OBJECT_IDENTIFIER
argument_list|)
condition|)
block|{
comment|/**                  * Create a new document because child datasources may need                  * to be retrieved independently of parent doc.                  */
name|DocumentHandler
name|docHandler
init|=
operator|new
name|DocumentHandler
argument_list|(
name|map
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|docHandler
operator|.
name|process
argument_list|()
expr_stmt|;
name|documents
operator|.
name|addAll
argument_list|(
name|docHandler
operator|.
name|getDocuments
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addMapToDoc
argument_list|(
name|map
argument_list|)
expr_stmt|;
comment|/**                  * Add nested datasources of this datasource's data                  */
name|addNestedDataSource
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Adds a String-based field to a document.      *      * @param Type of field.      * @param Name of field.      * @param Value of field.      */
DECL|method|addFieldToDoc
specifier|private
name|void
name|addFieldToDoc
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
name|value
operator|=
name|StringUtils
operator|.
name|EMPTY_STRING
expr_stmt|;
if|if
condition|(
name|SearchConfiguration
operator|.
name|KEYWORD_FIELD_TYPE
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|SearchConfiguration
operator|.
name|UNINDEXED_FIELD_TYPE
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnIndexed
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|SearchConfiguration
operator|.
name|UNSTORED_FIELD_TYPE
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnStored
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a Reader-based field to a document.      *      * @param Name of field.      * @param Reader.      */
DECL|method|addFieldToDoc
specifier|private
name|void
name|addFieldToDoc
parameter_list|(
name|String
name|field
parameter_list|,
name|Reader
name|reader
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
name|field
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds documents to the IndexWriter.      */
DECL|method|addToWriter
specifier|private
name|void
name|addToWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|parentEncapsulation
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|n
init|=
name|documents
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|(
name|Document
operator|)
name|documents
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|Enumeration
name|e
init|=
name|d
operator|.
name|fields
argument_list|()
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|Field
name|f
init|=
operator|(
name|Field
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|f
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|IndexDataSource
operator|.
name|CONTAINER_IDENTIFIER
argument_list|)
operator|&&
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|IndexDataSource
operator|.
name|OBJECT_CLASS
argument_list|)
operator|&&
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|IndexDataSource
operator|.
name|OBJECT_IDENTIFIER
argument_list|)
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|n
init|=
name|documents
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
operator|(
name|Document
operator|)
name|documents
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

