package com.example.myweb.board.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.myweb.board.dto.FreeBoardDTO;
import com.example.myweb.board.entity.FreeBoardEntity;
import com.example.myweb.board.entity.FreeBoardFileEntity;
import com.example.myweb.board.entity.FreeBoardLikeEntity;
import com.example.myweb.board.repository.FreeBoardFileRepository;
import com.example.myweb.board.repository.FreeBoardLikeRepository;
import com.example.myweb.board.repository.FreeBoardRepository;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// DTO -> Entity
// Entity -> DTO

@Service
@RequiredArgsConstructor
public class FreeBoardService {
	private final FreeBoardRepository freeBoardRepository;
	private final FreeBoardFileRepository freeBoardFileRepository;
	private final FreeBoardLikeRepository freeBoardLikeRepository;
	private final UserRepository userRepository;

	public void save(FreeBoardDTO freeBoardDTO) throws IllegalStateException, IOException {
		// UserEntity를 UserRepository를 통해 조회합니다.
		UserEntity userEntity = userRepository
				.findByLoginidAndNickname(freeBoardDTO.getLoginid(), freeBoardDTO.getNickname()).get();

		// 파일 첨부 여부에 따라 로직 분리
		if (freeBoardDTO.getFreeboardFile().isEmpty()) {
			// 첨부 파일 없음.

			// 조회된 UserEntity를 사용하여 FreeBoardEntity를 생성합니다.
			FreeBoardEntity freeBoardEntity = FreeBoardEntity.toSaveEntity(freeBoardDTO, userEntity);

			// FreeBoardEntity를 저장합니다.
			freeBoardRepository.save(freeBoardEntity);
		} else {
			// 첨부 파일 있음.

			// 1. DTO에 담긴 파일을 꺼냄
			// 2. 파일의 이름 가져옴
			// 3. 서버 저장용 이름을 만든다
			// 내사진.jpg => 1287637126_내사진.jpg
			// 4. 저장 경로 설정
			// 5. 해당 경로에 파일 저장
			// 6. free_board_table에 해당 데이터 save 처리
			// 7. free_board_file_table에 해당 데이터 save처리
			// 조회된 UserEntity를 사용하여 FreeBoardEntity를 생성합니다.
			FreeBoardEntity freeBoardEntity = FreeBoardEntity.toSaveFileEntity(freeBoardDTO, userEntity);
			Long savedSeq = freeBoardRepository.save(freeBoardEntity).getSeq(); // 게시글의 seq
			FreeBoardEntity freeBoard = freeBoardRepository.findById(savedSeq).get(); // 게시글의 정보를 가져옴
			for (MultipartFile freeBoardFile : freeBoardDTO.getFreeboardFile()) {
				// MultipartFile freeBoardFile = freeBoardDTO.getFreeboardFile(); // 1.
				String originalFilename = freeBoardFile.getOriginalFilename(); // 2.
				String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3.
//				String savePath = "C:/springboot_img/" + storedFileName; // 4. C:/springboot_img/687416238_내사진.jpg
				String savePath = new File("src/main/resources/static/upload/").getAbsolutePath() + "/"
						+ storedFileName; // 4. C:/springboot_img/687416238_내사진.jpg

				File file = new File(savePath);
				file.getParentFile().mkdirs(); // 경로가 존재하지 않으면 생성
				freeBoardFile.transferTo(file);
				// freeBoardFile.transferTo(new File(savePath)); // 5.

				FreeBoardFileEntity freeBoardFileEntity = FreeBoardFileEntity.toFreeBoardFileEntity(freeBoard,
						originalFilename, storedFileName);
				freeBoardFileRepository.save(freeBoardFileEntity);
			}
		}

	}

	@Transactional
	public List<FreeBoardDTO> findAll() {
		List<FreeBoardEntity> freeBoardEntityList = freeBoardRepository.findAll();
		List<FreeBoardDTO> freeBoardDTOList = new ArrayList<>();

		for (FreeBoardEntity freeBoardEntity : freeBoardEntityList) {
			freeBoardDTOList.add(FreeBoardDTO.toFreeBoardDTO(freeBoardEntity));
		}

		return freeBoardDTOList;
	}

	@Transactional
	public void incrementViews(Long seq) {
		freeBoardRepository.incrementViews(seq);
	}

	@Transactional
	public FreeBoardDTO findBySeq(Long seq) {
		Optional<FreeBoardEntity> optionalFreeBoardEntity = freeBoardRepository.findById(seq);
		if (optionalFreeBoardEntity.isPresent()) {
			FreeBoardEntity freeBoardEntity = optionalFreeBoardEntity.get();
			FreeBoardDTO freeBoardDTO = FreeBoardDTO.toFreeBoardDTO(freeBoardEntity);

			return freeBoardDTO;
		} else {
			return null;
		}
	}

	@Transactional
	public FreeBoardDTO update(FreeBoardDTO freeBoardDTO) {
		UserEntity userEntity = userRepository
				.findByLoginidAndNickname(freeBoardDTO.getLoginid(), freeBoardDTO.getNickname()).get();
		FreeBoardEntity freeBoareEntity = FreeBoardEntity.toUpdateEntity(freeBoardDTO, userEntity);
		freeBoardRepository.save(freeBoareEntity);

		return findBySeq(freeBoardDTO.getSeq());
	}

	public void delete(Long seq) {
		freeBoardRepository.deleteById(seq);
	}

	@Transactional
	public Page<FreeBoardDTO> paging(Pageable pageable) {
		int page = pageable.getPageNumber();
		if (page < 0) {
			page = 0;
		} else {
			page -= 1;
		}
		int pageLimit = 3; // 한 페이지에 보여줄 글 갯수
		// 한페이지당 3개씩 글을 보여주고 정렬 기준은 seq 기준으로 내림차순 정렬
		// page 위치에 있는 값은 0부터 시작
		Page<FreeBoardEntity> freeBoardEntities = freeBoardRepository
				.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "seq")));

		System.out.println("freeBoardEntities.getContent() = " + freeBoardEntities.getContent()); // 요청 페이지에 해당하는 글
		System.out.println("freeBoardEntities.getTotalElements() = " + freeBoardEntities.getTotalElements()); // 전체 글갯수
		System.out.println("freeBoardEntities.getNumber() = " + freeBoardEntities.getNumber()); // DB로 요청한 페이지 번호
		System.out.println("freeBoardEntities.getTotalPages() = " + freeBoardEntities.getTotalPages()); // 전체 페이지 갯수
		System.out.println("freeBoardEntities.getSize() = " + freeBoardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
		System.out.println("freeBoardEntities.hasPrevious() = " + freeBoardEntities.hasPrevious()); // 이전 페이지 존재 여부
		System.out.println("freeBoardEntities.isFirst() = " + freeBoardEntities.isFirst()); // 첫 페이지 여부
		System.out.println("freeBoardEntities.isLast() = " + freeBoardEntities.isLast()); // 마지막 페이지 여부

		// 목록: seq, nickname, title, views, likeCount, createdTime
		// seq, tag, title, createdTime, views, lickCount, nickname
		Page<FreeBoardDTO> freeBoardDTOS = freeBoardEntities.map(freeBoard -> new FreeBoardDTO(freeBoard.getSeq(),
				freeBoard.getTag(), freeBoard.getTitle(), freeBoard.getCreatedTime(), freeBoard.getViews(),
				freeBoard.getLikeCount(), freeBoard.getNickname()));

		return freeBoardDTOS;
	}

	// 좋아요 기능
	@Transactional
	public boolean toggleLike(Long freeBoard_seq, String loginid) {
	    // 게시글과 사용자 정보를 Optional로 조회합니다.
	    Optional<FreeBoardEntity> optionalBoard = freeBoardRepository.findById(freeBoard_seq);
	    Optional<UserEntity> optionalUser = userRepository.findByLoginid(loginid);

	    // Optional에서 데이터를 가져올 수 있는지 확인합니다.
	    if (optionalBoard.isPresent() && optionalUser.isPresent()) {
	        FreeBoardEntity board = optionalBoard.get();
	        UserEntity user = optionalUser.get();

	        // 좋아요 여부를 확인하기 위해 좋아요 엔티티를 조회합니다.
	        Optional<FreeBoardLikeEntity> optionalLike = freeBoardLikeRepository.findByUserAndFreeBoardEntity(user, board);

	        if (optionalLike.isPresent()) {
	            // 이미 좋아요를 누른 상태이면 좋아요 취소 처리합니다.
	            FreeBoardLikeEntity like = optionalLike.get();
	            freeBoardLikeRepository.delete(like); // 좋아요 엔티티 삭제
	            board.setLikeCount(board.getLikeCount() - 1); // 게시글의 좋아요 수 감소
	        } else {
	            // 좋아요를 누르지 않은 상태이면 좋아요 추가 처리합니다.
	            FreeBoardLikeEntity like = new FreeBoardLikeEntity();
	            like.setUser(user);
	            like.setFreeBoardEntity(board);
	            freeBoardLikeRepository.save(like); // 좋아요 엔티티 저장
	            board.setLikeCount(board.getLikeCount() + 1); // 게시글의 좋아요 수 증가
	        }

	        // 게시글 엔티티 저장 (좋아요 수 변경 반영)
	        freeBoardRepository.save(board);

	        return true; // 성공적으로 처리됨을 반환
	    }

	    return false; // 게시글 또는 사용자가 존재하지 않음을 반환
	}

	public boolean isLikedByUser(Long boardSeq, String loginid) {
	    Optional<FreeBoardEntity> optionalBoard = freeBoardRepository.findById(boardSeq);
	    Optional<UserEntity> optionalUser = userRepository.findByLoginid(loginid);
	    if (optionalBoard.isPresent() && optionalUser.isPresent()) {
	        FreeBoardEntity board = optionalBoard.get();
	        UserEntity user = optionalUser.get();
	        return freeBoardLikeRepository.findByUserAndFreeBoardEntity(user, board).isPresent();
	    }
	    return false;
	}

}
