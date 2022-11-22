import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  return (
    <Router>
      <div>
        <nav>
          <Link to="/">Home</Link>
          <Link to="/foo">Foo</Link>
          <Link to="/bar">Bar</Link>
        </nav>
        <Switch>
          <Route exact path="/" component={Home} />
          <Route exact path="/foo" component={Foo} />
          <Route exact path="/bar" component={Bar} />
        </Switch>
      </div>
    </Router>
);