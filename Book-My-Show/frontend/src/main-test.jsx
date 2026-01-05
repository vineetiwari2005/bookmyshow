import React from 'react';
import ReactDOM from 'react-dom/client';

console.log('🔴 MINIMAL TEST STARTING...');

const root = document.getElementById('root');

if (!root) {
  console.error('❌ NO ROOT ELEMENT!');
  alert('ERROR: No root element found!');
} else {
  console.log('✅ Root element exists');
  
  try {
    const rootInstance = ReactDOM.createRoot(root);
    console.log('✅ ReactDOM.createRoot successful');
    
    rootInstance.render(
      <div style={{
        padding: '50px',
        textAlign: 'center',
        fontFamily: 'Arial, sans-serif',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        minHeight: '100vh',
        color: 'white'
      }}>
        <h1 style={{ fontSize: '48px', marginBottom: '20px' }}>
          ✅ REACT IS WORKING!
        </h1>
        <p style={{ fontSize: '24px', marginBottom: '30px' }}>
          If you can see this, React is rendering successfully!
        </p>
        <div style={{
          background: 'rgba(255,255,255,0.1)',
          padding: '20px',
          borderRadius: '10px',
          maxWidth: '600px',
          margin: '0 auto'
        }}>
          <h2>Next Steps:</h2>
          <ol style={{ textAlign: 'left', fontSize: '18px', lineHeight: '2' }}>
            <li>Rename main-test.jsx to main.jsx.bak</li>
            <li>Rename main.jsx.original to main.jsx</li>
            <li>Reload the page</li>
          </ol>
        </div>
        <p style={{ marginTop: '30px', fontSize: '16px' }}>
          Check browser console (F12) for detailed logs
        </p>
      </div>
    );
    
    console.log('✅ React render() called successfully');
    console.log('✅ MINIMAL TEST COMPLETE - REACT IS WORKING!');
    
  } catch (error) {
    console.error('❌ ERROR during React render:', error);
    alert('React Error: ' + error.message);
    root.innerHTML = `
      <div style="padding: 50px; font-family: Arial; color: red;">
        <h1>React Render Failed!</h1>
        <p>Error: ${error.message}</p>
        <p>Check console for details</p>
      </div>
    `;
  }
}
