// Example: Multi-level Headings Document
// This example demonstrates using multiple heading levels to create a structured document
// with hierarchical sections and subsections

<Document>
  <Section pageSize="A4" pageMarginTop="1440" pageMarginBottom="1440" pageMarginLeft="1440" pageMarginRight="1440">
    
    {/* Document Title */}
    <Heading level="1">Software Design Document</Heading>
    
    <Paragraph><Text>This document outlines the architecture and design specifications for the new system.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 1: Introduction */}
    <Heading level="1">1. Introduction</Heading>
    
    <Paragraph><Text>This section provides an overview of the project scope and objectives.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Purpose */}
    <Heading level="2">1.1 Purpose</Heading>
    
    <Paragraph><Text>The purpose of this document is to define the system architecture, design decisions, and implementation guidelines for the development team.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Scope */}
    <Heading level="2">1.2 Scope</Heading>
    
    <Paragraph><Text>This document covers the following areas:</Text></Paragraph>
    <BulletedList>
      <ListItem level="0"><Paragraph><Text>System architecture and components</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Database design and schema</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>API specifications</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Security considerations</Text></Paragraph></ListItem>
    </BulletedList>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Definitions */}
    <Heading level="2">1.3 Definitions and Acronyms</Heading>
    
    <Paragraph><Text>Key terms used throughout this document:</Text></Paragraph>
    
    {/* Level 3: API */}
    <Heading level="3">1.3.1 API</Heading>
    <Paragraph><Text>Application Programming Interface - a set of protocols for building software applications.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: REST */}
    <Heading level="3">1.3.2 REST</Heading>
    <Paragraph><Text>Representational State Transfer - an architectural style for distributed systems.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 1: System Architecture */}
    <Heading level="1">2. System Architecture</Heading>
    
    <Paragraph><Text>The system follows a three-tier architecture pattern consisting of presentation, business logic, and data access layers.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Frontend Layer */}
    <Heading level="2">2.1 Frontend Layer</Heading>
    
    <Paragraph><Text>The presentation layer is built using modern web technologies to provide a responsive user interface.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: Technologies */}
    <Heading level="3">2.1.1 Technologies Used</Heading>
    <Paragraph><Text>The frontend stack includes:</Text></Paragraph>
    <BulletedList>
      <ListItem level="0"><Paragraph><Text>React 18 for UI components</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>TypeScript for type safety</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Redux for state management</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Material-UI for design system</Text></Paragraph></ListItem>
    </BulletedList>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: Component Structure */}
    <Heading level="3">2.1.2 Component Structure</Heading>
    <Paragraph><Text>Components are organized in a modular hierarchy following atomic design principles.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 4: Atoms */}
    <Heading level="4">2.1.2.1 Atoms</Heading>
    <Paragraph><Text>Basic building blocks such as buttons, inputs, and labels.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 4: Molecules */}
    <Heading level="4">2.1.2.2 Molecules</Heading>
    <Paragraph><Text>Combinations of atoms forming simple components like form fields with labels.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 4: Organisms */}
    <Heading level="4">2.1.2.3 Organisms</Heading>
    <Paragraph><Text>Complex components composed of molecules, such as navigation bars and data tables.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Backend Layer */}
    <Heading level="2">2.2 Backend Layer</Heading>
    
    <Paragraph><Text>The business logic layer handles all server-side operations and business rules.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: API Design */}
    <Heading level="3">2.2.1 API Design</Heading>
    <Paragraph><Text>RESTful API endpoints follow industry best practices for resource naming and HTTP methods.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: Authentication */}
    <Heading level="3">2.2.2 Authentication</Heading>
    <Paragraph><Text>JWT-based authentication with refresh token mechanism for secure access control.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Data Layer */}
    <Heading level="2">2.3 Data Layer</Heading>
    
    <Paragraph><Text>The data access layer manages all database operations and data persistence.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: Database Schema */}
    <Heading level="3">2.3.1 Database Schema</Heading>
    <Paragraph><Text>PostgreSQL database with normalized tables and appropriate indexes for query optimization.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: Caching Strategy */}
    <Heading level="3">2.3.2 Caching Strategy</Heading>
    <Paragraph><Text>Redis is used for caching frequently accessed data to reduce database load.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 1: Security Considerations */}
    <Heading level="1">3. Security Considerations</Heading>
    
    <Paragraph><Text>Security is implemented at multiple layers to protect against common vulnerabilities.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Input Validation */}
    <Heading level="2">3.1 Input Validation</Heading>
    <Paragraph><Text>All user inputs are validated and sanitized on both client and server sides.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Data Encryption */}
    <Heading level="2">3.2 Data Encryption</Heading>
    <Paragraph><Text>Sensitive data is encrypted at rest and in transit using industry-standard algorithms.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: Transport Security */}
    <Heading level="3">3.2.1 Transport Security</Heading>
    <Paragraph><Text>TLS 1.3 is enforced for all client-server communications.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 3: Data at Rest */}
    <Heading level="3">3.2.2 Data at Rest</Heading>
    <Paragraph><Text>AES-256 encryption is used for sensitive data stored in the database.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 1: Deployment */}
    <Heading level="1">4. Deployment Strategy</Heading>
    
    <Paragraph><Text>The application is deployed using containerization and orchestration technologies.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Containerization */}
    <Heading level="2">4.1 Containerization</Heading>
    <Paragraph><Text>Docker containers ensure consistent environments across development, staging, and production.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: Orchestration */}
    <Heading level="2">4.2 Orchestration</Heading>
    <Paragraph><Text>Kubernetes manages container deployment, scaling, and load balancing.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 2: CI/CD Pipeline */}
    <Heading level="2">4.3 CI/CD Pipeline</Heading>
    <Paragraph><Text>Automated testing and deployment through GitHub Actions ensures code quality and rapid releases.</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Level 1: Conclusion */}
    <Heading level="1">5. Conclusion</Heading>
    
    <Paragraph><Text>This design document provides a comprehensive overview of the system architecture and implementation approach. The modular design ensures maintainability and scalability for future enhancements.</Text></Paragraph>
    
  </Section>
</Document>
