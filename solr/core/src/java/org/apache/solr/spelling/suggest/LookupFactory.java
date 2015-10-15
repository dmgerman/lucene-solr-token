begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|suggest
operator|.
name|Lookup
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
name|FSDirectory
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|core
operator|.
name|SolrCore
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
name|spelling
operator|.
name|suggest
operator|.
name|jaspell
operator|.
name|JaspellLookupFactory
import|;
end_import

begin_comment
comment|/**  * Suggester factory for creating {@link Lookup} instances.  */
end_comment

begin_class
DECL|class|LookupFactory
specifier|public
specifier|abstract
class|class
name|LookupFactory
block|{
comment|/** Default lookup implementation to use for SolrSuggester */
DECL|field|DEFAULT_FILE_BASED_DICT
specifier|public
specifier|static
name|String
name|DEFAULT_FILE_BASED_DICT
init|=
name|JaspellLookupFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**    * Create a Lookup using config options in<code>params</code> and     * current<code>core</code>    */
DECL|method|create
specifier|public
specifier|abstract
name|Lookup
name|create
parameter_list|(
name|NamedList
name|params
parameter_list|,
name|SolrCore
name|core
parameter_list|)
function_decl|;
comment|/**     *<p>Returns the filename in which the in-memory data structure is stored</p>    *<b>NOTE:</b> not all {@link Lookup} implementations store in-memory data structures    * */
DECL|method|storeFileName
specifier|public
specifier|abstract
name|String
name|storeFileName
parameter_list|()
function_decl|;
comment|/** Non-null if this sugggester created a temp dir, needed only during build */
DECL|field|tmpBuildDir
specifier|private
specifier|static
name|FSDirectory
name|tmpBuildDir
decl_stmt|;
DECL|method|getTempDir
specifier|protected
specifier|static
specifier|synchronized
name|FSDirectory
name|getTempDir
parameter_list|()
block|{
if|if
condition|(
name|tmpBuildDir
operator|==
literal|null
condition|)
block|{
comment|// Lazy init
name|String
name|tempDirPath
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tempDirPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Java has no temporary folder property (java.io.tmpdir)?"
argument_list|)
throw|;
block|}
try|try
block|{
name|tmpBuildDir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|tempDirPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
return|return
name|tmpBuildDir
return|;
block|}
block|}
end_class

end_unit

