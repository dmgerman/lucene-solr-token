begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

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
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
comment|/**  * An IndexWriter that is configured via Solr config mechanisms.  * * @author yonik * @version $Id: SolrIndexWriter.java,v 1.9 2006/01/09 03:51:44 yonik Exp $ * @since solr 0.9 */
end_comment

begin_class
DECL|class|SolrIndexWriter
specifier|public
class|class
name|SolrIndexWriter
extends|extends
name|IndexWriter
block|{
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
name|SolrIndexWriter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|schema
name|IndexSchema
name|schema
decl_stmt|;
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
block|{
name|log
operator|.
name|fine
argument_list|(
literal|"Opened Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|setSimilarity
argument_list|(
name|schema
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
comment|// setUseCompoundFile(false);
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|setUseCompoundFile
argument_list|(
name|config
operator|.
name|useCompoundFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|maxBufferedDocs
operator|!=
operator|-
literal|1
condition|)
name|minMergeDocs
operator|=
name|config
operator|.
name|maxBufferedDocs
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|maxMergeDocs
operator|!=
operator|-
literal|1
condition|)
name|maxMergeDocs
operator|=
name|config
operator|.
name|maxMergeDocs
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|mergeFactor
operator|!=
operator|-
literal|1
condition|)
name|mergeFactor
operator|=
name|config
operator|.
name|mergeFactor
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|maxFieldLength
operator|!=
operator|-
literal|1
condition|)
name|maxFieldLength
operator|=
name|config
operator|.
name|maxFieldLength
expr_stmt|;
block|}
block|}
DECL|method|SolrIndexWriter
specifier|public
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrIndexWriter
specifier|public
name|SolrIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|create
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|name
argument_list|,
name|schema
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/*** use DocumentBuilder now...   private final void addField(Document doc, String name, String val) {       SchemaField ftype = schema.getField(name);        // we don't check for a null val ourselves because a solr.FieldType       // might actually want to map it to something.  If createField()       // returns null, then we don't store the field.        Field field = ftype.createField(val, boost);       if (field != null) doc.add(field);   }     public void addRecord(String[] fieldNames, String[] fieldValues) throws IOException {     Document doc = new Document();     for (int i=0; i<fieldNames.length; i++) {       String name = fieldNames[i];       String val = fieldNames[i];        // first null is end of list.  client can reuse arrays if they want       // and just write a single null if there is unused space.       if (name==null) break;        addField(doc,name,val);     }     addDocument(doc);   }   ******/
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|fine
argument_list|(
literal|"Closing Writer "
operator|+
name|name
argument_list|)
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|finalizer
name|void
name|finalizer
parameter_list|()
block|{
try|try
block|{
name|super
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
block|{}
block|}
block|}
end_class

end_unit

