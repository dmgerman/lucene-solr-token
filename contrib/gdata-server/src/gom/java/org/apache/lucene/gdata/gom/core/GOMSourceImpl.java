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
name|util
operator|.
name|LinkedList
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
name|GOMAttribute
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
name|GOMAuthor
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
name|GOMCategory
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
name|GOMContributor
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
name|GOMGenerator
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
name|GOMIcon
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
name|GOMId
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
name|GOMLogo
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
name|GOMNamespace
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
name|GOMRights
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
name|GOMSource
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
name|GOMSubtitle
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
name|GOMTitle
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
name|GOMUpdated
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
name|core
operator|.
name|utils
operator|.
name|AtomParserUtils
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

begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|GOMSourceImpl
specifier|public
class|class
name|GOMSourceImpl
extends|extends
name|AbstractGOMElement
implements|implements
name|GOMSource
block|{
DECL|field|authors
specifier|protected
name|List
argument_list|<
name|GOMAuthor
argument_list|>
name|authors
init|=
operator|new
name|LinkedList
argument_list|<
name|GOMAuthor
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|categories
specifier|protected
name|List
argument_list|<
name|GOMCategory
argument_list|>
name|categories
init|=
operator|new
name|LinkedList
argument_list|<
name|GOMCategory
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|links
specifier|protected
name|List
argument_list|<
name|GOMLink
argument_list|>
name|links
init|=
operator|new
name|LinkedList
argument_list|<
name|GOMLink
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|contributors
specifier|protected
name|List
argument_list|<
name|GOMContributor
argument_list|>
name|contributors
init|=
operator|new
name|LinkedList
argument_list|<
name|GOMContributor
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|generator
specifier|protected
name|GOMGenerator
name|generator
decl_stmt|;
DECL|field|id
specifier|protected
name|GOMId
name|id
decl_stmt|;
DECL|field|logo
specifier|protected
name|GOMLogo
name|logo
decl_stmt|;
DECL|field|rights
specifier|protected
name|GOMRights
name|rights
decl_stmt|;
DECL|field|subtitle
specifier|protected
name|GOMSubtitle
name|subtitle
decl_stmt|;
DECL|field|title
specifier|protected
name|GOMTitle
name|title
decl_stmt|;
DECL|field|updated
specifier|protected
name|GOMUpdated
name|updated
decl_stmt|;
DECL|field|icon
specifier|protected
name|GOMIcon
name|icon
decl_stmt|;
DECL|method|GOMSourceImpl
name|GOMSourceImpl
parameter_list|()
block|{
name|this
operator|.
name|localName
operator|=
name|LOCALNAME
expr_stmt|;
name|this
operator|.
name|qname
operator|=
operator|new
name|QName
argument_list|(
name|GOMNamespace
operator|.
name|ATOM_NS_URI
argument_list|,
name|this
operator|.
name|localName
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.core.AbstractGOMElement#getLocalName() 	 */
annotation|@
name|Override
DECL|method|getLocalName
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|this
operator|.
name|localName
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#addAuthor(org.apache.lucene.gdata.gom.GOMAuthor) 	 */
DECL|method|addAuthor
specifier|public
name|void
name|addAuthor
parameter_list|(
name|GOMAuthor
name|aAuthor
parameter_list|)
block|{
if|if
condition|(
name|aAuthor
operator|!=
literal|null
condition|)
name|this
operator|.
name|authors
operator|.
name|add
argument_list|(
name|aAuthor
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#addCategory(org.apache.lucene.gdata.gom.GOMCategory) 	 */
DECL|method|addCategory
specifier|public
name|void
name|addCategory
parameter_list|(
name|GOMCategory
name|aCategory
parameter_list|)
block|{
if|if
condition|(
name|aCategory
operator|!=
literal|null
condition|)
name|this
operator|.
name|categories
operator|.
name|add
argument_list|(
name|aCategory
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#addContributor(org.apache.lucene.gdata.gom.GOMContributor) 	 */
DECL|method|addContributor
specifier|public
name|void
name|addContributor
parameter_list|(
name|GOMContributor
name|aContributor
parameter_list|)
block|{
if|if
condition|(
name|aContributor
operator|!=
literal|null
condition|)
name|this
operator|.
name|contributors
operator|.
name|add
argument_list|(
name|aContributor
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#getAuthors() 	 *  	 */
DECL|method|getAuthors
specifier|public
name|List
argument_list|<
name|GOMAuthor
argument_list|>
name|getAuthors
parameter_list|()
block|{
return|return
name|this
operator|.
name|authors
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#getCategories() 	 *  	 */
DECL|method|getCategories
specifier|public
name|List
argument_list|<
name|GOMCategory
argument_list|>
name|getCategories
parameter_list|()
block|{
return|return
name|this
operator|.
name|categories
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#getContributor() 	 */
DECL|method|getContributor
specifier|public
name|List
argument_list|<
name|GOMContributor
argument_list|>
name|getContributor
parameter_list|()
block|{
return|return
name|this
operator|.
name|contributors
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#getGenerator() 	 *  	 */
DECL|method|getGenerator
specifier|public
name|GOMGenerator
name|getGenerator
parameter_list|()
block|{
return|return
name|this
operator|.
name|generator
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#getId() 	 *  	 */
DECL|method|getId
specifier|public
name|GOMId
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#setGenerator(org.apache.lucene.gdata.gom.GOMGenerator) 	 *  	 */
DECL|method|setGenerator
specifier|public
name|void
name|setGenerator
parameter_list|(
name|GOMGenerator
name|aGenerator
parameter_list|)
block|{
name|this
operator|.
name|generator
operator|=
name|aGenerator
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#setIcon(org.apache.lucene.gdata.gom.GOMIcon) 	 *  	 */
DECL|method|setIcon
specifier|public
name|void
name|setIcon
parameter_list|(
name|GOMIcon
name|aIcon
parameter_list|)
block|{
name|this
operator|.
name|icon
operator|=
name|aIcon
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#setId(org.apache.lucene.gdata.gom.GOMId) 	 *  	 */
DECL|method|setId
specifier|public
name|void
name|setId
parameter_list|(
name|GOMId
name|aId
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|aId
expr_stmt|;
block|}
comment|/** 	 * @return the logo 	 *  	 */
DECL|method|getLogo
specifier|public
name|GOMLogo
name|getLogo
parameter_list|()
block|{
return|return
name|this
operator|.
name|logo
return|;
block|}
comment|/** 	 * @param aLogo 	 *            the logo to set 	 *  	 */
DECL|method|setLogo
specifier|public
name|void
name|setLogo
parameter_list|(
name|GOMLogo
name|aLogo
parameter_list|)
block|{
name|this
operator|.
name|logo
operator|=
name|aLogo
expr_stmt|;
block|}
comment|/** 	 * @return the rights 	 *  	 */
DECL|method|getRights
specifier|public
name|GOMRights
name|getRights
parameter_list|()
block|{
return|return
name|this
operator|.
name|rights
return|;
block|}
comment|/** 	 * @param aRights 	 *            the rights to set 	 *  	 */
DECL|method|setRights
specifier|public
name|void
name|setRights
parameter_list|(
name|GOMRights
name|aRights
parameter_list|)
block|{
name|rights
operator|=
name|aRights
expr_stmt|;
block|}
comment|/** 	 * @return the subtitle 	 *  	 */
DECL|method|getSubtitle
specifier|public
name|GOMSubtitle
name|getSubtitle
parameter_list|()
block|{
return|return
name|this
operator|.
name|subtitle
return|;
block|}
comment|/** 	 * @param aSubtitle 	 *            the subtitle to set 	 *  	 */
DECL|method|setSubtitle
specifier|public
name|void
name|setSubtitle
parameter_list|(
name|GOMSubtitle
name|aSubtitle
parameter_list|)
block|{
name|this
operator|.
name|subtitle
operator|=
name|aSubtitle
expr_stmt|;
block|}
comment|/** 	 * @return the title 	 *  	 */
DECL|method|getTitle
specifier|public
name|GOMTitle
name|getTitle
parameter_list|()
block|{
return|return
name|this
operator|.
name|title
return|;
block|}
comment|/** 	 * @param aTitle 	 *            the title to set 	 *  	 */
DECL|method|setTitle
specifier|public
name|void
name|setTitle
parameter_list|(
name|GOMTitle
name|aTitle
parameter_list|)
block|{
name|this
operator|.
name|title
operator|=
name|aTitle
expr_stmt|;
block|}
comment|/** 	 * @return the updated 	 *  	 */
DECL|method|getUpdated
specifier|public
name|GOMUpdated
name|getUpdated
parameter_list|()
block|{
return|return
name|this
operator|.
name|updated
return|;
block|}
comment|/** 	 * @param aUpdated 	 *            the updated to set 	 *  	 */
DECL|method|setUpdated
specifier|public
name|void
name|setUpdated
parameter_list|(
name|GOMUpdated
name|aUpdated
parameter_list|)
block|{
name|this
operator|.
name|updated
operator|=
name|aUpdated
expr_stmt|;
block|}
comment|/** 	 * @return the icon 	 *  	 */
DECL|method|getIcon
specifier|public
name|GOMIcon
name|getIcon
parameter_list|()
block|{
return|return
name|this
operator|.
name|icon
return|;
block|}
comment|/** 	 * @return the links 	 *  	 */
DECL|method|getLinks
specifier|public
name|List
argument_list|<
name|GOMLink
argument_list|>
name|getLinks
parameter_list|()
block|{
return|return
name|this
operator|.
name|links
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMSource#addLink(org.apache.lucene.gdata.gom.GOMLink) 	 */
DECL|method|addLink
specifier|public
name|void
name|addLink
parameter_list|(
name|GOMLink
name|aLink
parameter_list|)
block|{
if|if
condition|(
name|aLink
operator|==
literal|null
condition|)
return|return;
name|this
operator|.
name|links
operator|.
name|add
argument_list|(
name|aLink
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.core.AtomParser#processElementValue(java.lang.String) 	 */
DECL|method|processElementValue
specifier|public
name|void
name|processElementValue
parameter_list|(
name|String
name|aValue
parameter_list|)
block|{
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|UNEXPECTED_ELEMENT_VALUE
argument_list|,
name|this
operator|.
name|localName
argument_list|)
argument_list|)
throw|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.core.AtomParser#processEndElement() 	 */
DECL|method|processEndElement
specifier|public
name|void
name|processEndElement
parameter_list|()
block|{
comment|/* 		 * atom:feed elements MUST contain exactly one atom:id element. 		 */
if|if
condition|(
name|this
operator|.
name|id
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|MISSING_ELEMENT_CHILD
argument_list|,
name|this
operator|.
name|localName
argument_list|,
name|GOMId
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
comment|/* 		 * atom:feed elements MUST contain exactly one atom:title element. 		 */
if|if
condition|(
name|this
operator|.
name|title
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|MISSING_ELEMENT_CHILD
argument_list|,
name|this
operator|.
name|localName
argument_list|,
name|GOMTitle
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
comment|/* 		 * atom:feed elements MUST contain exactly one atom:updated element. 		 */
if|if
condition|(
name|this
operator|.
name|updated
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|MISSING_ELEMENT_CHILD
argument_list|,
name|this
operator|.
name|localName
argument_list|,
name|GOMUpdated
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
comment|/* 		 * atom:feed elements MUST contain one or more atom:author elements, 		 * unless all of the 		 */
if|if
condition|(
name|this
operator|.
name|authors
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|MISSING_ELEMENT_CHILD
argument_list|,
name|this
operator|.
name|localName
argument_list|,
name|GOMAuthor
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
comment|/* 		 * atom:feed elements MUST NOT contain more than one atom:link element 		 * with a rel attribute value of "alternate" that has the same 		 * combination of type and hreflang attribute values. 		 */
name|List
argument_list|<
name|GOMLink
argument_list|>
name|alternateLinks
init|=
operator|new
name|LinkedList
argument_list|<
name|GOMLink
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|GOMLink
name|link
range|:
name|this
operator|.
name|links
control|)
block|{
comment|/* 			 * atom:link elements MAY have a "rel" attribute that indicates the 			 * link relation type. If the "rel" attribute is not present, the 			 * link element MUST be interpreted as if the link relation type is 			 * "alternate". 			 */
if|if
condition|(
name|link
operator|.
name|getRel
argument_list|()
operator|==
literal|null
operator|||
name|link
operator|.
name|getRel
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"alternate"
argument_list|)
condition|)
name|alternateLinks
operator|.
name|add
argument_list|(
name|link
argument_list|)
expr_stmt|;
block|}
comment|/* 		 * atom:feed elements MUST NOT contain more than one atom:link element 		 * with a rel attribute value of "alternate" that has the same 		 * combination of type and hreflang attribute values. 		 */
if|if
condition|(
name|alternateLinks
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|GOMLink
name|link
range|:
name|alternateLinks
control|)
block|{
for|for
control|(
name|GOMLink
name|link2
range|:
name|alternateLinks
control|)
block|{
if|if
condition|(
name|link
operator|!=
name|link2
condition|)
if|if
condition|(
name|AtomParserUtils
operator|.
name|compareAlternateLinks
argument_list|(
name|link
argument_list|,
name|link2
argument_list|)
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|DUPLICATE_ELEMENT
argument_list|,
literal|"link with rel=\"alternate\" and same href and type attributes"
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.core.AtomParser#getChildParser(javax.xml.namespace.QName) 	 */
DECL|method|getChildParser
specifier|public
name|AtomParser
name|getChildParser
parameter_list|(
name|QName
name|aName
parameter_list|)
block|{
if|if
condition|(
name|aName
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMNamespace
operator|.
name|ATOM_NS_URI
argument_list|)
condition|)
block|{
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMId
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
comment|// atom:feed / atom:source elements MUST contain exactly one
comment|// atom:id element.
if|if
condition|(
name|this
operator|.
name|id
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|DUPLICATE_ELEMENT
argument_list|,
name|GOMId
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
name|this
operator|.
name|id
operator|=
operator|new
name|GOMIdImpl
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|id
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMTitle
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
comment|// atom:feed / atom:source elements MUST contain exactly one
comment|// atom:title
comment|// element.
if|if
condition|(
name|this
operator|.
name|title
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|DUPLICATE_ELEMENT
argument_list|,
name|GOMTitle
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
name|this
operator|.
name|title
operator|=
operator|new
name|GOMTitleImpl
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|title
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMAuthor
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
name|GOMAuthor
name|author
init|=
operator|new
name|GOMAuthorImpl
argument_list|()
decl_stmt|;
name|this
operator|.
name|authors
operator|.
name|add
argument_list|(
name|author
argument_list|)
expr_stmt|;
return|return
name|author
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMCategory
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
name|GOMCategory
name|category
init|=
operator|new
name|GOMCategoryImpl
argument_list|()
decl_stmt|;
name|this
operator|.
name|categories
operator|.
name|add
argument_list|(
name|category
argument_list|)
expr_stmt|;
return|return
name|category
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMContributor
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
name|GOMContributorImpl
name|impl
init|=
operator|new
name|GOMContributorImpl
argument_list|()
decl_stmt|;
name|this
operator|.
name|contributors
operator|.
name|add
argument_list|(
name|impl
argument_list|)
expr_stmt|;
return|return
name|impl
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMLink
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
name|GOMLinkImpl
name|impl
init|=
operator|new
name|GOMLinkImpl
argument_list|()
decl_stmt|;
name|this
operator|.
name|links
operator|.
name|add
argument_list|(
name|impl
argument_list|)
expr_stmt|;
return|return
name|impl
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMSubtitle
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
name|GOMSubtitleImpl
name|impl
init|=
operator|new
name|GOMSubtitleImpl
argument_list|()
decl_stmt|;
comment|/* 				 * atom:feed elements MUST NOT contain more than one 				 * atom:subtitle element. 				 */
if|if
condition|(
name|this
operator|.
name|subtitle
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|DUPLICATE_ELEMENT
argument_list|,
name|GOMSubtitle
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
name|this
operator|.
name|subtitle
operator|=
name|impl
expr_stmt|;
return|return
name|this
operator|.
name|subtitle
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMUpdated
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|updated
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|DUPLICATE_ELEMENT
argument_list|,
name|GOMUpdated
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
name|GOMUpdated
name|updatedImpl
init|=
operator|new
name|GOMUpdatedImpl
argument_list|()
decl_stmt|;
name|this
operator|.
name|updated
operator|=
name|updatedImpl
expr_stmt|;
return|return
name|this
operator|.
name|updated
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMLogo
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|logo
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|DUPLICATE_ELEMENT
argument_list|,
name|GOMLogo
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
name|this
operator|.
name|logo
operator|=
operator|new
name|GOMLogoImpl
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|logo
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMIcon
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|icon
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|DUPLICATE_ELEMENT
argument_list|,
name|GOMIcon
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
name|this
operator|.
name|icon
operator|=
operator|new
name|GOMIconImpl
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|icon
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMGenerator
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|generator
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|DUPLICATE_ELEMENT
argument_list|,
name|GOMGenerator
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
name|this
operator|.
name|generator
operator|=
operator|new
name|GOMGeneratorImpl
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|generator
return|;
block|}
if|if
condition|(
name|aName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|GOMRights
operator|.
name|LOCALNAME
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|rights
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|DUPLICATE_ELEMENT
argument_list|,
name|GOMRights
operator|.
name|LOCALNAME
argument_list|)
argument_list|)
throw|;
name|this
operator|.
name|rights
operator|=
operator|new
name|GOMRightsImpl
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|rights
return|;
block|}
block|}
throw|throw
operator|new
name|GDataParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AtomParser
operator|.
name|URECOGNIZED_ELEMENT_CHILD
argument_list|,
name|this
operator|.
name|localName
argument_list|,
name|aName
operator|.
name|getLocalPart
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMElement#writeAtomOutput(org.apache.lucene.gdata.gom.writer.GOMStaxWriter) 	 */
DECL|method|writeAtomOutput
specifier|public
name|void
name|writeAtomOutput
parameter_list|(
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|aStreamWriter
operator|.
name|writeStartElement
argument_list|(
name|this
operator|.
name|localName
argument_list|,
name|this
operator|.
name|extensionAttributes
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|GOMAttribute
argument_list|>
name|xmlNamespaceAttributes
init|=
name|getXmlNamespaceAttributes
argument_list|()
decl_stmt|;
for|for
control|(
name|GOMAttribute
name|attribute
range|:
name|xmlNamespaceAttributes
control|)
block|{
name|aStreamWriter
operator|.
name|writeAttribute
argument_list|(
name|attribute
argument_list|)
expr_stmt|;
block|}
name|writeInnerAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
name|aStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @param aStreamWriter 	 * @throws XMLStreamException 	 */
DECL|method|writeInnerAtomOutput
specifier|protected
name|void
name|writeInnerAtomOutput
parameter_list|(
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|this
operator|.
name|id
operator|!=
literal|null
condition|)
name|this
operator|.
name|id
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|title
operator|!=
literal|null
condition|)
name|this
operator|.
name|title
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|subtitle
operator|!=
literal|null
condition|)
name|this
operator|.
name|subtitle
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
for|for
control|(
name|GOMAuthor
name|authors
range|:
name|this
operator|.
name|authors
control|)
block|{
name|authors
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|GOMCategory
name|category
range|:
name|this
operator|.
name|categories
control|)
block|{
name|category
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|GOMContributor
name|contributor
range|:
name|this
operator|.
name|contributors
control|)
block|{
name|contributor
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|GOMLink
name|link
range|:
name|this
operator|.
name|links
control|)
block|{
name|link
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|rights
operator|!=
literal|null
condition|)
name|this
operator|.
name|rights
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|updated
operator|!=
literal|null
condition|)
name|this
operator|.
name|updated
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|logo
operator|!=
literal|null
condition|)
name|this
operator|.
name|logo
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|icon
operator|!=
literal|null
condition|)
name|this
operator|.
name|icon
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|generator
operator|!=
literal|null
condition|)
name|this
operator|.
name|generator
operator|.
name|writeAtomOutput
argument_list|(
name|aStreamWriter
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMElement#writeRssOutput(org.apache.lucene.gdata.gom.writer.GOMStaxWriter) 	 */
DECL|method|writeRssOutput
specifier|public
name|void
name|writeRssOutput
parameter_list|(
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
block|{
comment|// no rss output
block|}
block|}
end_class

end_unit

