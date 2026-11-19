import { render, screen } from '@testing-library/react-native';
import { CommentPage } from './comments-section';

describe('CommentPage', () => {
  it('renders the commenter name', () => {
    render(<CommentPage name="DuckyFan" comment="Great duck!" />);

    expect(screen.getByText('DuckyFan')).toBeTruthy();
  });

  it('renders the comment text', () => {
    render(<CommentPage name="Alex" comment="Looks like a mallard." />);

    expect(screen.getByText('Looks like a mallard.')).toBeTruthy();
  });

  it('updates the displayed name and comment when props change', () => {
    const { rerender } = render(
      <CommentPage name="Alex" comment="Original comment" />,
    );

    rerender(<CommentPage name="Morgan" comment="Updated comment" />);

    expect(screen.getByText('Morgan')).toBeTruthy();
    expect(screen.getByText('Updated comment')).toBeTruthy();
  });
});
