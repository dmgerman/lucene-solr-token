begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ProvidedServiceConfig
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
name|gdata
operator|.
name|utils
operator|.
name|ProvidedServiceStub
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|Category
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|ExtensionProfile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|HtmlTextConstruct
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|extensions
operator|.
name|EventEntry
import|;
end_import

begin_class
DECL|class|TestDomIndexable
specifier|public
class|class
name|TestDomIndexable
extends|extends
name|TestCase
block|{
DECL|method|testConstructor
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
try|try
block|{
operator|new
name|DomIndexable
argument_list|(
operator|new
name|ServerBaseEntry
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no service config"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotIndexableException
name|e
parameter_list|)
block|{                                   }
name|ServerBaseEntry
name|e
init|=
operator|new
name|ServerBaseEntry
argument_list|()
decl_stmt|;
name|e
operator|.
name|setServiceConfig
argument_list|(
operator|new
name|ProvidedServiceConfig
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|DomIndexable
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no extension profile"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e1
parameter_list|)
block|{                                   }
catch|catch
parameter_list|(
name|NotIndexableException
name|e2
parameter_list|)
block|{
name|fail
argument_list|(
literal|"unexp. exception"
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|setServiceConfig
argument_list|(
operator|new
name|ProvidedServiceStub
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|DomIndexable
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotIndexableException
name|e1
parameter_list|)
block|{
name|fail
argument_list|(
literal|"unexp. exception"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.search.analysis.DomIndexable.applyPath(String)'      */
DECL|method|testApplyPath
specifier|public
name|void
name|testApplyPath
parameter_list|()
throws|throws
name|NotIndexableException
throws|,
name|XPathExpressionException
block|{
name|String
name|content
init|=
literal|"fooo bar<br>"
decl_stmt|;
name|ServerBaseEntry
name|entry
init|=
operator|new
name|ServerBaseEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setContent
argument_list|(
operator|new
name|HtmlTextConstruct
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setServiceConfig
argument_list|(
operator|new
name|ProvidedServiceStub
argument_list|()
argument_list|)
expr_stmt|;
name|Indexable
name|ind
init|=
operator|new
name|DomIndexable
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|ind
operator|.
name|applyPath
argument_list|(
literal|"/entry/content"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
argument_list|,
name|n
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|attr
init|=
name|ind
operator|.
name|applyPath
argument_list|(
literal|"/entry/content/@type"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|attr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"html"
argument_list|,
name|attr
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|attr
operator|instanceof
name|Attr
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

