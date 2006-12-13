begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
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
name|gom
operator|.
name|GOMLink
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
name|gom
operator|.
name|writer
operator|.
name|GOMStaxWriter
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
name|gom
operator|.
name|writer
operator|.
name|GOMOutputWriter
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

begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|GOMLinkImplTest
specifier|public
class|class
name|GOMLinkImplTest
extends|extends
name|TestCase
block|{
DECL|field|impl
specifier|private
name|GOMLinkImpl
name|impl
decl_stmt|;
comment|/** 	 * @see junit.framework.TestCase#setUp() 	 */
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|impl
operator|=
operator|new
name|GOMLinkImpl
argument_list|()
expr_stmt|;
block|}
DECL|method|testCommonFields
specifier|public
name|void
name|testCommonFields
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|this
operator|.
name|impl
operator|.
name|getQname
argument_list|()
argument_list|)
expr_stmt|;
name|QName
name|qname
init|=
name|this
operator|.
name|impl
operator|.
name|getQname
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|qname
argument_list|,
operator|new
name|QName
argument_list|(
name|GOMLink
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|this
operator|.
name|impl
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* 	 * Test method for 	 * 'org.apache.lucene.gdata.gom.core.GOMLinkImpl.processAttribute(QName, 	 * String)' 	 */
DECL|method|testProcessAttribute
specifier|public
name|void
name|testProcessAttribute
parameter_list|()
block|{
comment|// title
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"title"
argument_list|)
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"title"
argument_list|,
name|this
operator|.
name|impl
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"title"
argument_list|)
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"duplicated attribute"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
comment|// hreflang
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"hreflang"
argument_list|)
argument_list|,
literal|"hreflang"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hreflang"
argument_list|,
name|this
operator|.
name|impl
operator|.
name|getHrefLang
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"hreflang"
argument_list|)
argument_list|,
literal|"hreflang"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"duplicated attribute"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
comment|// href
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"href"
argument_list|)
argument_list|,
literal|"href"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"href"
argument_list|,
name|this
operator|.
name|impl
operator|.
name|getHref
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"href"
argument_list|)
argument_list|,
literal|"href"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"duplicated attribute"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
comment|// type
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|)
argument_list|,
literal|"type"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"type"
argument_list|,
name|this
operator|.
name|impl
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|)
argument_list|,
literal|"type"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"duplicated attribute"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
comment|// lenght
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"length"
argument_list|)
argument_list|,
literal|"noint"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must be an integer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"length"
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|,
name|this
operator|.
name|impl
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"length"
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"duplicated attribute"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
comment|//
comment|// rel
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"rel"
argument_list|)
argument_list|,
literal|"relation"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"relation"
argument_list|,
name|this
operator|.
name|impl
operator|.
name|getRel
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"rel"
argument_list|)
argument_list|,
literal|"relation"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"duplicated attribute"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
comment|/* 	 * Test method for 	 * 'org.apache.lucene.gdata.gom.core.GOMLinkImpl.processEndElement()' 	 */
DECL|method|testProcessEndElement
specifier|public
name|void
name|testProcessEndElement
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processEndElement
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"href is requiered but not set"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
name|this
operator|.
name|impl
operator|.
name|setHref
argument_list|(
literal|"/helloworld"
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processEndElement
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"href is not an absolute url"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|//
block|}
name|this
operator|.
name|impl
operator|.
name|xmlBase
operator|=
literal|"http://url"
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|processEndElement
argument_list|()
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|xmlBase
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|setHref
argument_list|(
literal|"http://www.apache.org"
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|processEndElement
argument_list|()
expr_stmt|;
block|}
comment|/* 	 * Test method for 	 * 'org.apache.lucene.gdata.gom.core.GOMLinkImpl.writeAtomOutput(GOMWriter)' 	 */
DECL|method|testWriteAtomOutput
specifier|public
name|void
name|testWriteAtomOutput
parameter_list|()
throws|throws
name|XMLStreamException
block|{
block|{
name|StringWriter
name|strWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|GOMOutputWriter
name|writer
init|=
operator|new
name|GOMStaxWriter
argument_list|(
name|strWriter
argument_list|)
decl_stmt|;
name|this
operator|.
name|impl
operator|.
name|writeAtomOutput
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<link href=\"\"/>"
argument_list|,
name|strWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|StringWriter
name|strWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|GOMOutputWriter
name|writer
init|=
operator|new
name|GOMStaxWriter
argument_list|(
name|strWriter
argument_list|)
decl_stmt|;
name|this
operator|.
name|impl
operator|.
name|setHref
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|setHrefLang
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|setLength
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|setRel
argument_list|(
literal|"NEXT"
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|setTitle
argument_list|(
literal|"myTitle"
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|setType
argument_list|(
literal|"myType"
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|writeAtomOutput
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strWriter
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"href=\"test\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strWriter
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"title=\"myTitle\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strWriter
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"hreflang=\"test1\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strWriter
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"type=\"myType\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strWriter
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"rel=\"NEXT\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strWriter
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"length=\"2\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* 	 * Test method for 	 * 'org.apache.lucene.gdata.gom.core.GOMLinkImpl.writeRssOutput(GOMWriter)' 	 */
DECL|method|testWriteRssOutput
specifier|public
name|void
name|testWriteRssOutput
parameter_list|()
throws|throws
name|XMLStreamException
block|{
block|{
name|StringWriter
name|strWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|GOMOutputWriter
name|writer
init|=
operator|new
name|GOMStaxWriter
argument_list|(
name|strWriter
argument_list|)
decl_stmt|;
name|this
operator|.
name|impl
operator|.
name|writeRssOutput
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|strWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|this
operator|.
name|impl
operator|.
name|setHref
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|setType
argument_list|(
literal|"testType"
argument_list|)
expr_stmt|;
name|this
operator|.
name|impl
operator|.
name|setRel
argument_list|(
literal|"enclosure"
argument_list|)
expr_stmt|;
name|StringWriter
name|strWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|GOMOutputWriter
name|writer
init|=
operator|new
name|GOMStaxWriter
argument_list|(
name|strWriter
argument_list|)
decl_stmt|;
name|this
operator|.
name|impl
operator|.
name|writeRssOutput
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<enclosure type=\"testType\" href=\"test\"/>"
argument_list|,
name|strWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|this
operator|.
name|impl
operator|.
name|setRel
argument_list|(
literal|"comments"
argument_list|)
expr_stmt|;
name|StringWriter
name|strWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|GOMOutputWriter
name|writer
init|=
operator|new
name|GOMStaxWriter
argument_list|(
name|strWriter
argument_list|)
decl_stmt|;
name|this
operator|.
name|impl
operator|.
name|writeRssOutput
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<comments>test</comments>"
argument_list|,
name|strWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|this
operator|.
name|impl
operator|.
name|setRel
argument_list|(
literal|"alternate"
argument_list|)
expr_stmt|;
name|StringWriter
name|strWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|GOMOutputWriter
name|writer
init|=
operator|new
name|GOMStaxWriter
argument_list|(
name|strWriter
argument_list|)
decl_stmt|;
name|this
operator|.
name|impl
operator|.
name|writeRssOutput
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<link>test</link>"
argument_list|,
name|strWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* 	 * Test method for 	 * 'org.apache.lucene.gdata.gom.core.AbstractGOMElement.processElementValue(String)' 	 */
DECL|method|testProcessElementValue
specifier|public
name|void
name|testProcessElementValue
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|impl
operator|.
name|processElementValue
argument_list|(
literal|"hello world"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no content"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataParseException
name|e
parameter_list|)
block|{
comment|// TODO: handle exception
block|}
block|}
block|}
end_class

end_unit

