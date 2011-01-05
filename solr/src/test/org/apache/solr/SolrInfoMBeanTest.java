begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package

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
name|SolrInfoMBean
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
name|handler
operator|.
name|StandardRequestHandler
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
name|handler
operator|.
name|admin
operator|.
name|LukeRequestHandler
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
name|handler
operator|.
name|component
operator|.
name|SearchComponent
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
name|handler
operator|.
name|component
operator|.
name|SearchHandler
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
name|highlight
operator|.
name|DefaultSolrHighlighter
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
name|search
operator|.
name|LRUCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Enumeration
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

begin_comment
comment|/**  * A simple test used to increase code coverage for some standard things...  */
end_comment

begin_class
DECL|class|SolrInfoMBeanTest
specifier|public
class|class
name|SolrInfoMBeanTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets a list of everything we can find in the classpath and makes sure it has    * a name, description, etc...    */
annotation|@
name|Ignore
comment|// TODO: reenable once SOLR-2160 is fixed
DECL|method|testCallMBeanInfo
specifier|public
name|void
name|testCallMBeanInfo
parameter_list|()
throws|throws
name|Exception
block|{
comment|//    Object[] init = org.apache.solr.search.QParserPlugin.standardPlugins;
name|List
argument_list|<
name|Class
argument_list|>
name|classes
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
name|classes
operator|.
name|addAll
argument_list|(
name|getClassesForPackage
argument_list|(
name|StandardRequestHandler
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|classes
operator|.
name|addAll
argument_list|(
name|getClassesForPackage
argument_list|(
name|SearchHandler
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|classes
operator|.
name|addAll
argument_list|(
name|getClassesForPackage
argument_list|(
name|SearchComponent
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|classes
operator|.
name|addAll
argument_list|(
name|getClassesForPackage
argument_list|(
name|LukeRequestHandler
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|classes
operator|.
name|addAll
argument_list|(
name|getClassesForPackage
argument_list|(
name|DefaultSolrHighlighter
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|classes
operator|.
name|addAll
argument_list|(
name|getClassesForPackage
argument_list|(
name|LRUCache
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// System.out.println(classes);
name|int
name|checked
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Class
name|clazz
range|:
name|classes
control|)
block|{
if|if
condition|(
name|SolrInfoMBean
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
try|try
block|{
name|SolrInfoMBean
name|info
init|=
operator|(
name|SolrInfoMBean
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|//System.out.println( info.getClass() );
name|assertNotNull
argument_list|(
name|info
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
operator|.
name|getSourceId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
operator|.
name|getCategory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|instanceof
name|LRUCache
condition|)
block|{
continue|continue;
block|}
name|assertNotNull
argument_list|(
name|info
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// increase code coverage...
name|assertNotNull
argument_list|(
name|info
operator|.
name|getDocs
argument_list|()
operator|+
literal|""
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
operator|.
name|getStatistics
argument_list|()
operator|+
literal|""
argument_list|)
expr_stmt|;
name|checked
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ex
parameter_list|)
block|{
comment|// expected...
comment|//System.out.println( "unable to initalize: "+clazz );
block|}
block|}
block|}
name|assertTrue
argument_list|(
literal|"there are at least 10 SolrInfoMBean that should be found in the classpath, found "
operator|+
name|checked
argument_list|,
name|checked
operator|>
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|getClassesForPackage
specifier|private
specifier|static
name|List
argument_list|<
name|Class
argument_list|>
name|getClassesForPackage
parameter_list|(
name|String
name|pckgname
parameter_list|)
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|File
argument_list|>
name|directories
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|ClassLoader
name|cld
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|pckgname
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|resources
init|=
name|cld
operator|.
name|getResources
argument_list|(
name|path
argument_list|)
decl_stmt|;
while|while
condition|(
name|resources
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|resources
operator|.
name|nextElement
argument_list|()
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|directories
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|Class
argument_list|>
name|classes
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|directory
range|:
name|directories
control|)
block|{
if|if
condition|(
name|directory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|endsWith
argument_list|(
literal|".class"
argument_list|)
condition|)
block|{
name|String
name|clazzName
init|=
name|file
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|file
operator|.
name|length
argument_list|()
operator|-
literal|6
argument_list|)
decl_stmt|;
comment|// exclude Test classes that happen to be in these packages.
comment|// class.ForName'ing some of them can cause trouble.
if|if
condition|(
operator|!
name|clazzName
operator|.
name|endsWith
argument_list|(
literal|"Test"
argument_list|)
operator|&&
operator|!
name|clazzName
operator|.
name|startsWith
argument_list|(
literal|"Test"
argument_list|)
condition|)
block|{
name|classes
operator|.
name|add
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|pckgname
operator|+
literal|'.'
operator|+
name|clazzName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|classes
return|;
block|}
block|}
end_class

end_unit

