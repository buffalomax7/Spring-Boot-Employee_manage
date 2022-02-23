import { render, cleanup,screen } from '@testing-library/react';
import CreateOffer from '../../components/root/users/homeBusiness/Offer/CreateOffer';
import React from 'react'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

afterEach(cleanup);

it('renders without crashing', () => {

  render(<Router> <CreateOffer /></Router>);

});

