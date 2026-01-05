import React from 'react';

const TestPage = () => {
  console.log('✅ TestPage component rendered successfully!');
  
  return (
    <div style={{ 
      padding: '50px', 
      textAlign: 'center',
      fontFamily: 'Arial, sans-serif'
    }}>
      <h1 style={{ color: '#f84464', marginBottom: '20px' }}>
        🎬 BookMyShow Frontend is Working!
      </h1>
      <p style={{ fontSize: '18px', color: '#333' }}>
        If you can see this page, your React app is running correctly.
      </p>
      <div style={{ 
        marginTop: '30px', 
        padding: '20px', 
        backgroundColor: '#f5f5f5',
        borderRadius: '8px',
        maxWidth: '600px',
        margin: '30px auto'
      }}>
        <h3 style={{ color: '#333545' }}>Next Steps:</h3>
        <ol style={{ textAlign: 'left', lineHeight: '2' }}>
          <li>Open browser console (F12) to see debug logs</li>
          <li>Check for any error messages</li>
          <li>Navigate to <a href="/">Home Page</a></li>
        </ol>
      </div>
      <a 
        href="/" 
        style={{
          display: 'inline-block',
          marginTop: '20px',
          padding: '12px 30px',
          backgroundColor: '#f84464',
          color: 'white',
          textDecoration: 'none',
          borderRadius: '6px',
          fontWeight: 'bold'
        }}
      >
        Go to Home Page
      </a>
    </div>
  );
};

export default TestPage;
