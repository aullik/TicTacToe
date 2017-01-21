import { Tictactoe3DPage } from './app.po';

describe('tictactoe3-d App', function() {
  let page: Tictactoe3DPage;

  beforeEach(() => {
    page = new Tictactoe3DPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
